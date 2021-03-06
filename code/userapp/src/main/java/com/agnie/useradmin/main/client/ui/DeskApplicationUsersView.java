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
package com.agnie.useradmin.main.client.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.agnie.gwt.common.client.rpc.AsyncDP;
import com.agnie.gwt.common.client.widget.CustomListBox;
import com.agnie.gwt.common.client.widget.CustomListBox.ChangeHandler;
import com.agnie.gwt.common.client.widget.DialogBox;
import com.agnie.gwt.common.client.widget.PageTitle;
import com.agnie.useradmin.common.client.base.Sort;
import com.agnie.useradmin.common.client.helper.RequestStatusWrapper;
import com.agnie.useradmin.common.client.renderer.RequestStatusWrapperCell;
import com.agnie.useradmin.common.widget.RolesManager;
import com.agnie.useradmin.main.client.presenter.ApplicationUserPresenter;
import com.agnie.useradmin.main.client.presenter.sahered.ui.AppUsersCellTable;
import com.agnie.useradmin.persistance.client.enums.RequestStatus;
import com.agnie.useradmin.persistance.client.helper.Permissions;
import com.agnie.useradmin.persistance.client.service.I18;
import com.agnie.useradmin.persistance.shared.proxy.RolePx;
import com.agnie.useradmin.persistance.shared.proxy.UserApplicationRegistrationPx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DeskApplicationUsersView extends Composite implements ApplicationUsersView {
	interface MyUiBinder extends UiBinder<Widget, DeskApplicationUsersView> {
	}

	interface MyStyle extends CssResource {
		String rgtMargin();
	}

	@UiField
	MyStyle										style;

	private static MyUiBinder					uiBinder				= GWT.create(MyUiBinder.class);
	MainPageResources							resource				= MainPageResources.INSTANCE;

	@UiField
	PageTitle									pageTitle;

	Button										apprProv				= new Button();
	Button										approve					= new Button();
	Button										delete					= new Button();

	@UiField
	HTMLPanel									reqStatusWrapContainer;
	@UiField
	HTMLPanel									userTblPan;
	@UiField
	HTMLPanel									buttonPanel;
	HTMLPanel									container;
	public AppUsersCellTable					uct;
	private RolesManager						rolesManager;
	@Inject
	ApplicationUserPresenter					presenter;

	private CustomListBox<RequestStatusWrapper>	reqStatWrap;
	private List<RequestStatusWrapper>			reqStatusWrapList		= new ArrayList<RequestStatusWrapper>();

	private List<RolePx>						totalRolesList			= new ArrayList<RolePx>();
	private List<RolePx>						totalAdminRolesList		= new ArrayList<RolePx>();
	private DialogBox							popUpPan;
	UserApplicationRegistrationPx				uarSel;
	UserApplicationRegistrationPx				uarSelAdmin;
	ClickHandler								updateRolesHandler		= new ClickHandler() {

																			@Override
																			public void onClick(ClickEvent event) {
																				presenter.updateUserRoles(uarSel, rolesManager.getSelUnsel().getSelected());
																			}
																		};

	ClickHandler								updateAdminRolesHandler	= new ClickHandler() {

																			@Override
																			public void onClick(ClickEvent event) {
																				presenter.updateUserAdminRoles(uarSelAdmin, rolesManager.getSelUnsel().getSelected());
																			}
																		};

	@Inject
	public DeskApplicationUsersView(AppUsersCellTable uct, RolesManager rolesManager) {
		this.uct = uct;
		this.rolesManager = rolesManager;
		container = (HTMLPanel) uiBinder.createAndBindUi(this);
		initWidget(container);
		userTblPan.add(uct);
		initPopUp();

		reqStatWrap = new CustomListBox<RequestStatusWrapper>(new RequestStatusWrapperCell());
		reqStatWrap.setList(getReqStatusList());
		reqStatusWrapContainer.add(reqStatWrap);

		reqStatWrap.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange() {
				refreshPage();
			}
		});

		rolesManager.getCancelBtn().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popUpPan.hide();
			}
		});

	}

	private void initApprProvButton() {
		apprProv.setText(com.agnie.useradmin.main.client.I18.messages.apprProv());
		apprProv.addStyleName("grey-button");
		apprProv.addStyleName(style.rgtMargin());
		buttonPanel.add(apprProv);
		apprProv.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final List<String> ids = new ArrayList<String>();
				Set<UserApplicationRegistrationPx> userAppRegPxSet = DeskApplicationUsersView.this.uct.getSelectionModel().getSelectedSet();
				if (!(userAppRegPxSet.isEmpty())) {
					for (UserApplicationRegistrationPx userApplicationRegistrationPx : userAppRegPxSet) {
						ids.add(userApplicationRegistrationPx.getId());
					}
				}
				if (!(ids.isEmpty())) {
					presenter.approveProvUserAppReg(ids);
				}
			}
		});
	}

	private void initApprButton() {
		approve.setText(com.agnie.useradmin.main.client.I18.messages.approve());
		approve.addStyleName("grey-button");
		approve.addStyleName(style.rgtMargin());
		buttonPanel.add(approve);
		approve.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final List<String> ids = new ArrayList<String>();
				Set<UserApplicationRegistrationPx> userAppRegPxSet = DeskApplicationUsersView.this.uct.getSelectionModel().getSelectedSet();
				if (!(userAppRegPxSet.isEmpty())) {
					for (UserApplicationRegistrationPx userApplicationRegistrationPx : userAppRegPxSet) {
						ids.add(userApplicationRegistrationPx.getId());
					}
				}
				if (!(ids.isEmpty())) {
					presenter.approveUserAppReg(ids);
				}
			}
		});
	}

	private void initdeleteButton() {
		delete.setText(com.agnie.useradmin.main.client.I18.messages.delete());
		delete.addStyleName("red-button");
		delete.addStyleName(style.rgtMargin());
		buttonPanel.add(delete);
		delete.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final List<String> ids = new ArrayList<String>();
				Set<UserApplicationRegistrationPx> userAppRegPxSet = DeskApplicationUsersView.this.uct.getSelectionModel().getSelectedSet();
				if (!(userAppRegPxSet.isEmpty())) {
					for (UserApplicationRegistrationPx userApplicationRegistrationPx : userAppRegPxSet) {
						ids.add(userApplicationRegistrationPx.getId());
					}
				}
				if (!(ids.isEmpty())) {
					presenter.markDisabledByUserAppRegId(ids);
				}
			}
		});
	}

	public void resetAppUserView() {
		this.popUpPan.hide();
	}

	private void initPopUp() {
		ClickHandler ch = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popUpPan.hide();
			}
		};
		popUpPan = new DialogBox(com.agnie.useradmin.main.client.I18.messages.manageUserRoles(), rolesManager);
		popUpPan.addCloseHandler(ch);
		popUpPan.hide();
	}

	public void getUserAppRoles(UserApplicationRegistrationPx uar) {
		this.uarSel = uar;
		this.rolesManager.getSelUnsel().clearSelUnselList();
		this.rolesManager.clearUpdateHandlers();
		this.rolesManager.addUpdateClickHandler(updateRolesHandler);
		this.presenter.getSelUserApplicationRoles(uar);// on success calls
														// setSelRolesList
		this.popUpPan.show();
		popUpPan.center();

	}

	public void getUserAppAdminRoles(UserApplicationRegistrationPx uar) {
		this.uarSelAdmin = uar;
		this.rolesManager.getSelUnsel().clearSelUnselList();
		this.rolesManager.clearUpdateHandlers();
		this.rolesManager.addUpdateClickHandler(updateAdminRolesHandler);
		this.presenter.getSelUserApplicationAdminRoles(uar);// on success calls
															// setSelAdminRolesList
		this.popUpPan.show();
		popUpPan.center();

	}

	private List<RolePx> getCloneList(List<RolePx> list) {
		List<RolePx> cloned = new ArrayList<RolePx>();
		if (!(list.isEmpty())) {
			for (RolePx rolePx : list) {
				cloned.add(rolePx);
			}
		}
		return cloned;
	}

	public void setRolePxList(List<RolePx> list) {
		if ((list != null) && !(list.isEmpty())) {
			this.totalRolesList = list;
		} else {
			GWT.log("In AppUserViewImpl setRolePxList() null");
		}
	}

	public void setAdminRolePxList(List<RolePx> list) {
		if ((list != null) && !(list.isEmpty())) {
			this.totalAdminRolesList = list;
		} else {
			GWT.log("In AppUserViewImpl setAdminRolePxList() null");
		}
	}

	public void setSelRolesList(List<RolePx> list) {
		if ((list != null) && !(list.isEmpty())) {
			List<RolePx> avlRolesList = new ArrayList<RolePx>();
			for (RolePx rolePx : totalRolesList) {
				if (!(list.contains(rolePx))) {
					avlRolesList.add(rolePx);
				}
			}
			this.rolesManager.getSelUnsel().setAvailabelData(avlRolesList);
			this.rolesManager.getSelUnsel().setSelectedData(list);
		} else {
			this.rolesManager.getSelUnsel().setAvailabelData(getCloneList(totalRolesList));
		}
	}

	public void setSelAdminRolesList(List<RolePx> list) {
		if ((list != null) && !(list.isEmpty())) {
			List<RolePx> avlRolesList = new ArrayList<RolePx>();
			for (RolePx rolePx : totalAdminRolesList) {
				if (!(list.contains(rolePx))) {
					avlRolesList.add(rolePx);
				}
			}
			this.rolesManager.getSelUnsel().setAvailabelData(avlRolesList);
			this.rolesManager.getSelUnsel().setSelectedData(list);
		} else {
			this.rolesManager.getSelUnsel().setAvailabelData(getCloneList(totalAdminRolesList));
		}
	}

	public List<RequestStatusWrapper> getReqStatusList() {
		reqStatusWrapList.add(new RequestStatusWrapper(I18.messages.all()));
		for (RequestStatus value : RequestStatus.values()) {
			reqStatusWrapList.add(new RequestStatusWrapper(value));
		}
		return reqStatusWrapList;
	}

	@Override
	public void setDataProvider(AsyncDP<UserApplicationRegistrationPx> dataProvider) {
		uct.setDataProvider(dataProvider);
	}

	@Override
	public void clearSelection() {
		uct.clearSelection();
	}

	@Override
	public void refreshPage() {
		uct.refresh();
	}

	@Override
	public void initialize() {
		uct.initialize();
		buttonPanel.clear();
		if (presenter.checkPermission(Permissions.APPLICATION_USER_REG)) {
			initApprProvButton();
			initApprButton();
		}

		if (presenter.checkPermission(Permissions.APPLICATION_USER_DEREG)) {
			initdeleteButton();
		}
	}

	@Override
	public RequestStatusWrapper getStatusFilter() {
		return reqStatWrap.getSelectedItem();
	}

	@Override
	public Sort getSortInfo() {
		return uct.getSortColumn();
	}
}
