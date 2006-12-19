/*
 * adminTool.GroupsTab 
 *
 *   Copyright 2006 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */
package src.adminTool.groupPanel;

// Java imports

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import ome.model.meta.ExperimenterGroup;

import layout.TableLayout;

import src.adminTool.model.Model;
import src.adminTool.ui.GroupList;
import src.adminTool.ui.GroupMembershipList;
import src.adminTool.ui.ImageFactory;
import src.adminTool.ui.UserList;
import src.adminTool.ui.messenger.MessageBox;

// Third-party libraries

// Application-internal dependencies

/**
 * 
 * 
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0 <small> (<b>Internal version:</b> $Revision$Date: $)
 *          </small>
 * @since OME3.0
 */
public class GroupsTab extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -8311862619538272312L;

    private UserList userList;

    private GroupMembershipList groupMembershipList;

    private GroupList groupList;

    private JLabel groupNameLabel;

    private JLabel descriptionLabel;

    private JLabel userListLabel;

    private JLabel groupListLabel;

    private JLabel userGroupListLabel;

    private JTextField groupName;

    private JTextArea description;

    private JButton saveBtn;

    private JButton setDefaultBtn;

    private JButton addUserBtn;

    private JButton removeUserBtn;

    private JButton addGroupBtn;

    private JButton removeGroupBtn;

    private Model model;

    private GroupsTabController controller;

    public ArrayList getUsers() {
        return groupMembershipList.getUsers();
    }

    public void setToTop() {
        groupName.requestFocusInWindow();
    }

    public void showErrorMessage(String errorMsg) {
        MessageBox msg = new MessageBox(this.getLocationOnScreen(), "Warning",
                errorMsg);
        setToTop();
    }

    public void clearForm() {
        groupName.setText("");
        description.setText("");
        groupMembershipList.clear();
    }

    public void selectCurrentGroup() {
        groupList.selectCurrentGroup();
    }

    public void refreshMembershipList() {
        String grp = groupList.getSelectedGroup();
        if (grp != null) {
            groupMembershipList.setGroup(grp);
        }
    }

    public void refresh() {
        groupMembershipList.clear();
        userList.filterOff();
        userList.refresh();
        groupList.filterOff();
        groupList.refresh();
    }

    public void setGroupNameEditable(boolean isEditable) {
        groupName.setEditable(isEditable);
    }

    public String getSelectedGroup() {
        return groupList.getSelectedGroup();
    }

    public String getSelectedUser() {
        return userList.getSelectedUser();
    }

    public String getSelectedMember() {
        return groupMembershipList.getSelectedUser();
    }

    public String getGroupName() {
        return groupName.getText();
    }

    public String getGroupDescription() {
        return description.getText();
    }

    public void setGroupDetails(ExperimenterGroup group) {
        groupName.setText(group.getName());
        description.setText(group.getDescription());
        groupMembershipList.setGroup(group.getName());
        groupList.selectCurrentGroup();
        userList.filterUsersByGroup(group.getName());
    }

    public void setController(GroupsTabController controller) {
        this.controller = controller;
        userList.setController(controller);
        groupList.setController(controller);
        saveBtn.addActionListener(new SaveAction(controller));
        addGroupBtn.addActionListener(new GroupAction(GroupAction.ADD,
                controller));
        removeGroupBtn.addActionListener(new GroupAction(GroupAction.REMOVE,
                controller));
        addUserBtn
                .addActionListener(new UserAction(UserAction.ADD, controller));
        removeUserBtn.addActionListener(new UserAction(UserAction.REMOVE,
                controller));
        setDefaultBtn.addActionListener(new SetDefaultAction(controller));
        groupList.selectFirstGroup();
    }

    public GroupsTab(Model model) {
        this.model = model;
        createUIElements();
        setPermissions();
        buildUI();
    }

    void createUIElements() {
        createUserList();
        createGroupList();
        createLabels();
        createTextFields();
        createActionButtons();
    }

    void createUserList() {
        groupMembershipList = new GroupMembershipList(model);
        userList = new UserList(model);
    }

    void createGroupList() {
        groupList = new GroupList(model);
    }

    void createLabels() {
        groupNameLabel = new JLabel("Group Name");
        descriptionLabel = new JLabel("Description");
        groupListLabel = new JLabel("Groups");
        groupListLabel.setHorizontalAlignment(SwingConstants.LEFT);
        userListLabel = new JLabel("Users");
        userListLabel.setHorizontalAlignment(SwingConstants.LEFT);
        userGroupListLabel = new JLabel("Group contains ");
        userGroupListLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    void createTextFields() {
        groupName = new JTextField("Groupname");
        description = new JTextArea("Description");
        description.setLineWrap(true);
        description.setWrapStyleWord(true);

    }

    void createActionButtons() {
        saveBtn = new JButton("Save");
        setDefaultBtn = new JButton("Set Default Group");
        
        ImageIcon addUserIcon = ImageFactory.get().image(
                ImageFactory.RIGHT_ARROW);
        ImageIcon removeUserIcon = ImageFactory.get().image(
                ImageFactory.LEFT_ARROW);
        ImageIcon addGroupIcon = ImageFactory.get()
                .image(ImageFactory.ADD_USER);
        ImageIcon removeGroupIcon = ImageFactory.get().image(
                ImageFactory.REMOVE_USER);
        addUserBtn = new JButton(addUserIcon);
        removeUserBtn = new JButton(removeUserIcon);
        addGroupBtn = new JButton(addGroupIcon);
        removeGroupBtn = new JButton(removeGroupIcon);

    }

    void setPermissions() {
        if (!model.isSystemUser()) {
            saveBtn.setEnabled(false);
            addUserBtn.setEnabled(false);
            removeUserBtn.setEnabled(false);
            addGroupBtn.setEnabled(false);
            removeGroupBtn.setEnabled(false);
        } else {
            saveBtn.setEnabled(true);
            addUserBtn.setEnabled(true);
            removeUserBtn.setEnabled(true);
            addGroupBtn.setEnabled(true);
            removeGroupBtn.setEnabled(true);
        }
    }

    JPanel createLabelText(JLabel label, JTextField text, int textSize) {
        double size[][] = { { 0.2, 0.05, TableLayout.FILL }, { textSize } };

        text.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(size));
        panel.add(label, "0, 0, l, t");
        panel.add(text, " 2, 0");
        return panel;
    }

    JPanel createLabelText(JLabel label, JTextArea text, int textSize) {
        double size[][] = { { 0.2, 0.05, TableLayout.FILL }, { textSize } };

        text.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(size));
        panel.add(label, "0, 0, l, t");
        panel.add(text, " 2, 0");
        return panel;
    }

    void buildUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createLabelText(groupNameLabel, groupName, 20));
        panel.add(Box.createRigidArea(new Dimension(5, 5)));
        panel.add(createLabelText(descriptionLabel, description, 60));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalStrut(100));
        buttonPanel.add(setDefaultBtn);
        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(saveBtn);
        panel.add(buttonPanel);
        panel.add(Box.createVerticalStrut(50));

        JPanel userGroupListButtonPanel = new JPanel();
        userGroupListButtonPanel.setLayout(new BoxLayout(
                userGroupListButtonPanel, BoxLayout.Y_AXIS));
        userGroupListButtonPanel.add(addUserBtn);
        userGroupListButtonPanel.add(removeUserBtn);

        JPanel userGroupPanel = new JPanel();
        userGroupPanel
                .setLayout(new BoxLayout(userGroupPanel, BoxLayout.Y_AXIS));
        userGroupPanel.add(userGroupListLabel);
        userGroupPanel.add(groupMembershipList);

        JPanel userGroupListPanel = new JPanel();
        userGroupListPanel.setLayout(new BoxLayout(userGroupListPanel,
                BoxLayout.X_AXIS));
        userGroupListPanel.add(userGroupListButtonPanel);
        userGroupListPanel.add(userGroupPanel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(panel);
        infoPanel.add(userGroupListPanel);

        JPanel groupListPanel = new JPanel();
        groupListPanel
                .setLayout(new BoxLayout(groupListPanel, BoxLayout.Y_AXIS));
        groupListPanel.add(groupListLabel);
        groupListPanel.add(groupList);

        JPanel groupListButtonPanel = new JPanel();
        groupListButtonPanel.setLayout(new BoxLayout(groupListButtonPanel,
                BoxLayout.Y_AXIS));
        groupListButtonPanel.add(addGroupBtn);
        // groupListButtonPanel.add(removeGroupBtn);

        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
        groupPanel.add(groupListButtonPanel);
        groupPanel.add(Box.createHorizontalStrut(20));
        groupPanel.add(groupListPanel);

        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        usersPanel.add(userListLabel);
        usersPanel.add(userList);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setLayout(new BoxLayout(spacerPanel, BoxLayout.X_AXIS));
        spacerPanel.add(Box.createHorizontalStrut(60));
        spacerPanel.add(usersPanel);

        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.add(groupPanel);
        listContainer.add(spacerPanel);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.add(listContainer);
        container.add(Box.createHorizontalStrut(50));
        container.add(infoPanel);
        container.add(Box.createHorizontalStrut(10));
        this.setLayout(new BorderLayout());
        this.add(container);
    }

}
