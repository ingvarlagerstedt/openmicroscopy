import unittest, time, os, datetime
import tempfile

import omero

from django.http import QueryDict
from django.conf import settings

from webgateway import views as webgateway_views
from webadmin import views as webadmin_views
from webadmin.forms import LoginForm, GroupForm, ExperimenterForm
                   
from webadmin.controller.experimenter import BaseExperimenter
from webadmin.controller.group import BaseGroup
from webadmin_test_library import WebTest, fakeRequest


class WebAdminTest(WebTest):
    
    def test_isServerOn(self):
        from omeroweb.webadmin.views import _isServerOn
        if not _isServerOn('localhost', 4064):
            self.fail('Server is offline')
            
    def test_checkVersion(self):
        from omeroweb.webadmin.views import _checkVersion
        if not _checkVersion('localhost', 4064):
            self.fail('Client version does not match server')
    
    def test_loginFromRequest(self):
        params = {
            'username': 'root',
            'password': self.root_password,
            'server':1,
            'ssl':'on'
        }        
        request = fakeRequest(method="post", params=params)
        
        blitz = settings.SERVER_LIST.get(pk=request.REQUEST.get('server')) 
        request.session['server'] = blitz.id
        request.session['host'] = blitz.host
        request.session['port'] = blitz.port
        request.session['username'] = request.REQUEST.get('username').encode('utf-8').strip()
        request.session['password'] = request.REQUEST.get('password').encode('utf-8').strip()
        request.session['ssl'] = (True, False)[request.REQUEST.get('ssl') is None]

        conn = webgateway_views.getBlitzConnection(request, useragent="TEST.webadmin")
        if conn is None:
            self.fail('Cannot connect')
        webgateway_views._session_logout(request, request.session.get('server'))
        if conn.isConnected() and conn.keepAlive():
            self.fail('Cnnection was not closed')

    def test_loginFromForm(self):
        params = {
            'username': 'root',
            'password': self.root_password,
            'server':1,
            'ssl':'on'
        }        
        request = fakeRequest(method="post", params=params)
        
        form = LoginForm(data=request.REQUEST.copy())        
        if form.is_valid():
            
            blitz = settings.SERVER_LIST.get(pk=form.cleaned_data['server']) 
            request.session['server'] = blitz.id
            request.session['host'] = blitz.host
            request.session['port'] = blitz.port
            request.session['username'] = form.cleaned_data['username'].strip()
            request.session['password'] = form.cleaned_data['password'].strip()
            request.session['ssl'] = form.cleaned_data['ssl']

            conn = webgateway_views.getBlitzConnection(request, useragent="TEST.webadmin")
            if conn is None:
                self.fail('Cannot connect')            
            webgateway_views._session_logout(request, request.session.get('server'))
            if conn.isConnected() and conn.keepAlive():
                self.fail('Cnnection was not closed')
            
        else:
            errors = form.errors.as_text()
            self.fail(errors)
            
    def test_loginFailure(self):
        params = {
            'username': 'notauser',
            'password': 'nonsence',
            'server':1
        }        
        request = fakeRequest(method="post", params=params)
        
        form = LoginForm(data=request.REQUEST.copy())        
        if form.is_valid():
            blitz = settings.SERVER_LIST.get(pk=form.cleaned_data['server']) 
            request.session['server'] = blitz.id
            request.session['host'] = blitz.host
            request.session['port'] = blitz.port
            request.session['username'] = form.cleaned_data['username'].strip()
            request.session['password'] = form.cleaned_data['password'].strip()
            request.session['ssl'] = form.cleaned_data['ssl']

            conn = webgateway_views.getBlitzConnection(request, useragent="TEST.webadmin")
            if conn is not None:
                self.fail('This user does not exist. Login failure error!')
                webgateway_views._session_logout(request, request.session.get('server'))

        else:
            errors = form.errors.as_text()
            self.fail(errors)            
    
    def test_createGroups(self):        
        conn = self.rootconn
        uuid = conn._sessionUuid
        
        #private group
        params = {
            "name":"webadmin_test_group_private %s" % uuid,
            "description":"test group",
            "owners": [0L],
            "permissions":0
        }
        request = fakeRequest(method="post", params=params)
        gid = _createGroup(request, conn)
           
        #check if group created
        controller = BaseGroup(conn, gid)
        perm = controller.getActualPermissions()
        self.assertEquals(params['name'], controller.group.name)
        self.assertEquals(params['description'], controller.group.description)
        self.assertEquals(sorted(params['owners']), sorted(controller.owners))
        self.assertEquals(params['permissions'], perm)
        
        #read-only group
        params = {
            "name":"webadmin_test_group_read-only %s" % uuid,
            "description":"test group",
            "owners": [0L],
            "permissions":1,
            "readonly":True
        }
        request = fakeRequest(method="post", params=params)
        gid = _createGroup(request, conn)
              
        #check if group created
        controller = BaseGroup(conn, gid)
        perm = controller.getActualPermissions()
        self.assertEquals(params['name'], controller.group.name)
        self.assertEquals(params['description'], controller.group.description)
        self.assertEquals(sorted(params['owners']), sorted(controller.owners))
        self.assertEquals(params['permissions'], perm)
        self.assertEquals(params['readonly'], controller.isReadOnly())
    
    def test_createExperimenters(self):        
        conn = self.rootconn
        uuid = conn._sessionUuid
        
        #private group
        params = {
            "name":"webadmin_test_group_private %s" % uuid,
            "description":"test group",
            "owners": [0L],
            "permissions":0
        }
        request = fakeRequest(method="post", params=params)
        gid = _createGroup(request, conn)
        
        params = {
            "omename":"webadmin_test_user %s" % uuid,
            "first_name":"uuid",
            "middle_name": "uuid",
            "last_name":"uuid",
            "email":"user_%s@domain.com" % uuid,
            "institution":"Laboratory",
            "active":True,
            "default_group":gid,
            "other_groups":[gid],
            "password":"ome",
            "confirmation":"ome" 
        }
        request = fakeRequest(method="post", params=params)
        eid = _createExperimenter(request, conn)
        
        #check if experimenter created
        controller = BaseExperimenter(conn, eid)
        self.assertEquals(params['omename'], controller.experimenter.omeName)
        self.assertEquals(params['first_name'], controller.experimenter.firstName)
        self.assertEquals(params['middle_name'], controller.experimenter.middleName)
        self.assertEquals(params['last_name'], controller.experimenter.lastName)
        self.assertEquals(params['email'], controller.experimenter.email)
        self.assertEquals(params['institution'], controller.experimenter.institution)
        self.assert_(not controller.experimenter.isAdmin())
        self.assertEquals(params['active'], controller.experimenter.isActive())
        self.assertEquals(params['default_group'], controller.defaultGroup)        
        self.assertEquals(sorted(params['other_groups']), sorted(controller.otherGroups))
        
        params = {
            "omename":"webadmin_test_admin %s" % uuid,
            "first_name":"uuid",
            "middle_name": "uuid",
            "last_name":"uuid",
            "email":"admin_%s@domain.com" % uuid,
            "institution":"Laboratory",
            "administrator": True,
            "active":True,
            "default_group":gid,
            "other_groups":[0,gid],
            "password":"ome",
            "confirmation":"ome" 
        }
        request = fakeRequest(method="post", params=params)
        eid = _createExperimenter(request, conn)
        
        #check if experimenter created
        controller = BaseExperimenter(conn, eid)
        self.assertEquals(params['omename'], controller.experimenter.omeName)
        self.assertEquals(params['first_name'], controller.experimenter.firstName)
        self.assertEquals(params['middle_name'], controller.experimenter.middleName)
        self.assertEquals(params['last_name'], controller.experimenter.lastName)
        self.assertEquals(params['email'], controller.experimenter.email)
        self.assertEquals(params['institution'], controller.experimenter.institution)
        self.assertEquals(params['administrator'], controller.experimenter.isAdmin())
        self.assertEquals(params['active'], controller.experimenter.isActive())
        self.assertEquals(params['default_group'], controller.defaultGroup)        
        self.assertEquals(sorted(params['other_groups']), sorted(controller.otherGroups))
        
        params = {
            "omename":"webadmin_test_off %s" % uuid,
            "first_name":"uuid",
            "middle_name": "uuid",
            "last_name":"uuid",
            "email":"off_%s@domain.com" % uuid,
            "institution":"Laboratory",
            "default_group":gid,
            "other_groups":[gid],
            "password":"ome",
            "confirmation":"ome" 
        }
        request = fakeRequest(method="post", params=params)
        eid = _createExperimenter(request, conn)
        
        #check if experimenter created
        controller = BaseExperimenter(conn, eid)
        self.assertEquals(params['omename'], controller.experimenter.omeName)
        self.assertEquals(params['first_name'], controller.experimenter.firstName)
        self.assertEquals(params['middle_name'], controller.experimenter.middleName)
        self.assertEquals(params['last_name'], controller.experimenter.lastName)
        self.assertEquals(params['email'], controller.experimenter.email)
        self.assertEquals(params['institution'], controller.experimenter.institution)
        self.assert_(not controller.experimenter.isAdmin())
        self.assert_(not controller.experimenter.isActive())
        self.assertEquals(params['default_group'], controller.defaultGroup)        
        self.assertEquals(sorted(params['other_groups']), sorted(controller.otherGroups))
        
        
####################################
# helpers
    
def _createGroup(request, conn):
    #create group
    controller = BaseGroup(conn)
    name_check = conn.checkGroupName(request.REQUEST.get('name'))
    form = GroupForm(initial={'experimenters':controller.experimenters}, data=request.POST.copy(), name_check=name_check)
    if form.is_valid():
        name = form.cleaned_data['name']
        description = form.cleaned_data['description']
        owners = form.cleaned_data['owners']
        permissions = form.cleaned_data['permissions']
        readonly = form.cleaned_data['readonly']
        return controller.createGroup(name, owners, permissions, readonly, description)
    else:
        errors = form.errors.as_text()
        self.fail(errors)

def _createExperimenter(request, conn):
    #create experimenter
    controller = BaseExperimenter(conn)
    name_check = conn.checkOmeName(request.REQUEST.get('omename'))
    email_check = conn.checkEmail(request.REQUEST.get('email'))

    initial={'with_password':True}

    exclude = list()            
    if len(request.REQUEST.getlist('other_groups')) > 0:
        others = controller.getSelectedGroups(request.REQUEST.getlist('other_groups'))   
        initial['others'] = others
        initial['default'] = [(g.id, g.name) for g in others]
        exclude.extend([g.id for g in others])

    available = controller.otherGroupsInitialList(exclude)
    initial['available'] = available
    form = ExperimenterForm(initial=initial, data=request.REQUEST.copy(), name_check=name_check, email_check=email_check)
    if form.is_valid():
        omename = form.cleaned_data['omename']
        firstName = form.cleaned_data['first_name']
        middleName = form.cleaned_data['middle_name']
        lastName = form.cleaned_data['last_name']
        email = form.cleaned_data['email']
        institution = form.cleaned_data['institution']
        admin = webadmin_views.toBoolean(form.cleaned_data['administrator'])
        active = webadmin_views.toBoolean(form.cleaned_data['active'])
        defaultGroup = form.cleaned_data['default_group']
        otherGroups = form.cleaned_data['other_groups']
        password = form.cleaned_data['password']
        return controller.createExperimenter(omename, firstName, lastName, email, admin, active, defaultGroup, otherGroups, password, middleName, institution)
    else:
        errors = form.errors.as_text()
        self.fail(errors)