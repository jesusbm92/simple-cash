package com.simplecash.ui.desktop.main;

import com.simplecash.dal.DatabaseManagerDAO;

import com.simplecash.exception.DbSchemaException;
import com.simplecash.object.Address;
import com.simplecash.object.Contact;
import com.simplecash.object.ContactInfo;
import com.simplecash.object.ContactInfoType;
import com.simplecash.ui.desktop.component.LeftPanel;
import com.simplecash.ui.desktop.component.contact.ContactForm;
import com.simplecash.ui.desktop.component.search.SearchPanel;
import com.simplecash.ui.desktop.event.ContactSearchEvent;
import com.simplecash.ui.desktop.event.ContactSearchEventListener;
import com.simplecash.ui.desktop.event.LookAndFeelChangeEvent;
import com.simplecash.ui.desktop.LookAndFeelOption;
import com.simplecash.ui.desktop.options.AboutForm;
import com.simplecash.ui.desktop.options.InterfaceOptionsForm;
import com.simplecash.ui.desktop.resourcebundle.ResourceBundleFactory;
import javaEventing.EventManager;
import javaEventing.interfaces.Event;
import javaEventing.interfaces.GenericEventListener;
import org.slf4j.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashSet;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

public class SimpleCashDialog extends JDialog implements ActionListener, TreeSelectionListener {
    private JPanel contentPane;
    private JSplitPane splitPane;

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ResourceBundle simpleCashDialogResourceBundle =
            PropertyResourceBundle.getBundle(SimpleCashDialog.class.getCanonicalName());
    private final ResourceBundle generalResourceBundle = ResourceBundleFactory.getGeneralBundle();

    public SimpleCashDialog() {
        setContentPane(contentPane);
        setModal(true);

        Dimension initialDimension = new Dimension(600, 400);
        setPreferredSize(initialDimension);
        setMinimumSize(initialDimension);
        setLocationRelativeTo(null);  // Center screen

        createMenu();

        LeftPanel leftPanel = new LeftPanel();
        leftPanel.getTree().addTreeSelectionListener(this);
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(new AboutForm().getPanel());

        // Register for events
        EventManager.registerEventListener(new GenericEventListener() {
            @Override
            public void eventTriggered(Object sender, Event event) {
                lookAndFeelChangeEventTriggered(sender, event);
            }
        }, LookAndFeelChangeEvent.class);
    }

    /**
     * Create the menu for the dialog.
     */
    private void createMenu() {
        // http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html

        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu menu_file = new JMenu(simpleCashDialogResourceBundle.getString("menu_file"));
        menu_file.setMnemonic(KeyEvent.VK_F);
        JMenuItem menu_file_login = new JMenuItem(simpleCashDialogResourceBundle.getString("menu_file_login"));
        menu_file_login.setActionCommand(SimpleCashDialogAction.menu_file_login_logout);
        menu_file_login.addActionListener(this);
        menu_file.add(menu_file_login);
        menu_file.addSeparator();
        JMenuItem menu_file_exit = new JMenuItem(simpleCashDialogResourceBundle.getString("menu_file_exit"));
        menu_file_exit.setActionCommand(SimpleCashDialogAction.menu_file_exit);
        menu_file_exit.addActionListener(this);
        menu_file.add(menu_file_exit);
        menuBar.add(menu_file);

        // Admin menu
        JMenu menu_admin = new JMenu(simpleCashDialogResourceBundle.getString("menu_admin"));
        JMenuItem menu_admin_createdb = new JMenuItem(simpleCashDialogResourceBundle.getString("menu_admin_createdb"));
        menu_admin_createdb.setActionCommand(SimpleCashDialogAction.menu_admin_createdb);
        menu_admin_createdb.addActionListener(this);
        menu_admin.add(menu_admin_createdb);
        JMenuItem menu_admin_updatedb = new JMenuItem(simpleCashDialogResourceBundle.getString("menu_admin_updatedb"));
        menu_admin_updatedb.setActionCommand(SimpleCashDialogAction.menu_admin_updatedb);
        menu_admin_updatedb.addActionListener(this);
        menu_admin.add(menu_admin_updatedb);
        JMenuItem menu_admin_populatedb = new JMenuItem(simpleCashDialogResourceBundle.getString("menu_admin_populatedb"));
        menu_admin_populatedb.setActionCommand(SimpleCashDialogAction.menu_admin_populatedb);
        menu_admin_populatedb.addActionListener(this);
        menu_admin.add(menu_admin_populatedb);
        menuBar.add(menu_admin);

        // Client menu
        JMenu menu_clients = new JMenu(generalResourceBundle.getString("Clients"));
        JMenuItem menu_clients_list = new JMenuItem();
        menuBar.add(menu_clients);

        // Supplier menu
        JMenu menu_suppliers = new JMenu(generalResourceBundle.getString("Suppliers"));
        JMenuItem menu_suppliers_list = new JMenuItem();
        menuBar.add(menu_suppliers);

        // Help menu
        menuBar.add(Box.createHorizontalGlue());
        JMenu menu_help = new JMenu(simpleCashDialogResourceBundle.getString("menu_help"));
        JMenuItem menu_help_about = new JMenuItem(simpleCashDialogResourceBundle.getString("menu_help_about"));
        menu_help.add(menu_help_about);
        menuBar.add(menu_help);

        setJMenuBar(menuBar);
    }

    public void actionPerformed(ActionEvent event) {
        if (SimpleCashDialogAction.menu_file_exit.equals(event.getActionCommand())) {
            this.dispose();
        } else if (SimpleCashDialogAction.menu_file_login_logout.equals(event.getActionCommand())) {
            // TODO: log in/out
        } else if (SimpleCashDialogAction.menu_admin_createdb.equals(event.getActionCommand())) {
            DatabaseManagerDAO.createDatabaseSchema();
        } else if (SimpleCashDialogAction.menu_admin_updatedb.equals(event.getActionCommand())) {
            DatabaseManagerDAO.updateDatabaseSchema();
        } else if (SimpleCashDialogAction.menu_admin_populatedb.equals(event.getActionCommand())) {
            try {
                DatabaseManagerDAO.populateWithTestData();
            } catch (DbSchemaException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    /**
     * Called whenever the value of the selection in the LeftPanel changes.
     * @param e the event that characterizes the change.
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree tree = (JTree)((JPanel)splitPane.getLeftComponent()).getComponent(0);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

        if (node == null) {
            //Nothing is selected.
            return;
        }

        // Note: path[0] is root node, which is always "Selection" and is not visible.
        Object[] path = e.getNewLeadSelectionPath().getPath();
        String path1 = path[1].toString();
        String path2 = null;
        if (path.length > 2) {
            path2 = path[2].toString();
        }

        if (path1.equals(generalResourceBundle.getString("MainPage"))) {
        }
        else if (path1.equals(generalResourceBundle.getString("Clients"))) {
            if (path2 != null) {
                if (path2.equals(generalResourceBundle.getString("New"))) {
                    Set<ContactInfo> contactInfos = new LinkedHashSet<ContactInfo>();
                    contactInfos.add(new ContactInfo()
                        .setContactInfoType(ContactInfoType.Type.Email).setType("HOME").setValue("home@email.com"));
                    contactInfos.add(new ContactInfo()
                            .setContactInfoType(ContactInfoType.Type.Email).setType("WORK").setValue("work@email.com"));
                    contactInfos.add(new ContactInfo()
                            .setContactInfoType(ContactInfoType.Type.Email).setType("WORK_2").setValue("work2@email.com"));
                    contactInfos.add(new ContactInfo()
                            .setContactInfoType(ContactInfoType.Type.Telephone).setType("Vodafone").setValue("555111222"));

                    Set<Address> addresses = new LinkedHashSet<Address>();
                    addresses.add(new Address().setName("Homez").setAddress1("addr1").setPostalCode("1234-900"));
                    addresses.add(new Address().setName("Vacation").setAddress1("333 holliday inn").setPostalCode("1234-900"));

                    Contact contact = new Contact();
                    contact.setContactInfos(contactInfos);
                    contact.setAddresses(addresses);

                    splitPane.setRightComponent(new ContactForm(contact).getMainPanel());
                } else if (path2.equals(generalResourceBundle.getString("List_verb"))) {
                    SearchPanel searchPanel = new SearchPanel();
                    searchPanel.registerSearchEventListener(new ContactSearchEventListener(), ContactSearchEvent.class);
                    splitPane.setRightComponent(searchPanel);
                }
            }
        } else if (path1.equals(generalResourceBundle.getString("Options"))) {
            if (path2 != null) {
                if (path2.equals(generalResourceBundle.getString("Interface"))) {
                    splitPane.setRightComponent(new InterfaceOptionsForm().getPanel());
                } else if (path2.equals(generalResourceBundle.getString("About"))) {
                    splitPane.setRightComponent(new AboutForm().getPanel());
                }
            }
        }
    }

    public void lookAndFeelChangeEventTriggered(Object sender, Event event) {

        if (event == null || !event.getClass().equals(LookAndFeelChangeEvent.class)) {
            return;
        }
        LookAndFeelOption lookAndFeelOption = (LookAndFeelOption)event.getPayload();

        try {
            UIManager.setLookAndFeel(lookAndFeelOption.getLookAndFeel().getCanonicalName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            logger.error("Error changing look and feel.", ex);
        }
    }
}
