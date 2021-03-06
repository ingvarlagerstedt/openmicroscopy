/*
 *   $Id$
 *
 *   Copyright 2008 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 *
 */
#include <boost_fixture.h>
#include <omero/model/ImageI.h>
#include <omero/model/TagAnnotationI.h>

using namespace std;
using namespace omero::api;
using namespace omero::model;
using namespace omero::rtypes;
using namespace omero::sys;

BOOST_AUTO_TEST_CASE( Counts )
{
    try {
        Fixture f;

	const omero::client_ptr client = f.login();
	ServiceFactoryPrx sf = client->getSession();
	IAdminPrx admin = sf->getAdminService();
	IQueryPrx query = sf->getQueryService();
	IUpdatePrx update = sf->getUpdateService();

	long usr = admin->getEventContext()->userId;

	ImagePtr img = new_ImageI();
        img->setName( rstring("name") );
	TagAnnotationIPtr tag = new TagAnnotationI();
	img->linkAnnotation( tag );
	img = ImageIPtr::dynamicCast( update->saveAndReturnObject( img ) );

        stringstream q;
        q << "select img from Image img ";
        q << "join fetch img.annotationLinksCountPerOwner ";
        q << "where img.id = ";
        q << img->getId()->getValue();
	img = ImageIPtr::dynamicCast( query->findByQuery(q.str(), 0) );

	BOOST_CHECK( img->getAnnotationLinksCountPerOwner()[usr] > 0 );

    } catch (const omero::ApiUsageException& aue) {
        cout << aue.message <<endl;
	BOOST_ERROR ( "api usage exception thrown" );
    } catch (const Ice::UnknownException& ue) {
        cout << ue << endl;
	BOOST_ERROR( "unknown exception thrown" );
    }
}
