/*******************************************************************************
 * Copyright (c) 2014 Agnie Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Agnie Technologies - initial API and implementation
 ******************************************************************************/
package com.agnie.useradmin.contextmgr.client;

import com.agnie.gwt.common.client.base.BasePageResourceLoader;
import com.agnie.useradmin.common.client.base.UserAdminBasePageResourceLoader;
import com.agnie.useradmin.contextmgr.client.injector.MVPInjector;
import com.agnie.useradmin.contextmgr.client.mvp.PlatformFactory;
import com.agnie.useradmin.contextmgr.client.ui.CtxMgrPageResourceLoader;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class ContextManager implements EntryPoint {
	private PlatformFactory			platformFactory		= GWT.create(PlatformFactory.class);
	CtxMgrPageResourceLoader		ctxMgrPageResource	= new CtxMgrPageResourceLoader();
	BasePageResourceLoader			bPResourceLoader	= new BasePageResourceLoader();
	UserAdminBasePageResourceLoader	dummy				= new UserAdminBasePageResourceLoader();

	@Override
	public void onModuleLoad() {
		GWT.log("ContextManager onmoduleload start");
		MVPInjector injector = platformFactory.getInjector();
		RootPanel pageLoader = injector.getClientFactory().getRootPanelFactory().getPageLoader();
		pageLoader.setVisible(false);

		AppLoader loader = injector.getAppLoader();
		loader.setViewFactory(platformFactory.getViewFactory());
		loader.start();
	}

}
