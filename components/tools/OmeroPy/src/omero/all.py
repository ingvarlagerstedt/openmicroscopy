#!/usr/bin/env python
"""

   Copyright 2010 Glencoe Software, Inc. All rights reserved.
   Use is subject to license terms supplied in LICENSE.txt

"""

import Ice, IceImport
import omero
if omero.__import_style__ is None:
    omero.__import_style__ = "all"
    import omero.rtypes
    import omero.callbacks
    IceImport.load("omero_System_ice")
    IceImport.load("omero_Collections_ice")
    IceImport.load("omero_ServicesF_ice")
    IceImport.load("omero_API_ice")
    IceImport.load("omero_Repositories_ice")
    IceImport.load("omero_SharedResources_ice")
    IceImport.load("omero_Scripts_ice")
    IceImport.load("omero_Tables_ice")
    IceImport.load("omero_api_Gateway_ice")
    IceImport.load("omero_api_IAdmin_ice")
    IceImport.load("omero_api_IConfig_ice")
    IceImport.load("omero_api_IContainer_ice")
    IceImport.load("omero_api_IDelete_ice")
    IceImport.load("omero_api_ILdap_ice")
    IceImport.load("omero_api_IMetadata_ice")
    IceImport.load("omero_api_IPixels_ice")
    IceImport.load("omero_api_IProjection_ice")
    IceImport.load("omero_api_IQuery_ice")
    IceImport.load("omero_api_IRenderingSettings_ice")
    IceImport.load("omero_api_IRepositoryInfo_ice")
    IceImport.load("omero_api_IRoi_ice")
    IceImport.load("omero_api_IScript_ice")
    IceImport.load("omero_api_ISession_ice")
    IceImport.load("omero_api_IShare_ice")
    IceImport.load("omero_api_ITimeline_ice")
    IceImport.load("omero_api_ITypes_ice")
    IceImport.load("omero_api_IUpdate_ice")
    IceImport.load("omero_api_Exporter_ice")
    IceImport.load("omero_api_JobHandle_ice")
    IceImport.load("omero_api_MetadataStore_ice")
    IceImport.load("omero_api_RawFileStore_ice")
    IceImport.load("omero_api_RawPixelsStore_ice")
    IceImport.load("omero_api_RenderingEngine_ice")
    IceImport.load("omero_api_Search_ice")
    IceImport.load("omero_api_ThumbnailStore_ice")
    IceImport.load("omero_Constants_ice")
    import omero_sys_ParametersI
    import omero_model_PermissionsI
