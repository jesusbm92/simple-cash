package com.simplecash.ui.desktop.component.contact;

import com.simplecash.dal.RepositoryFactory;
import com.simplecash.dal.repository.ContactRepository;
import com.simplecash.object.Contact;
import com.simplecash.ui.desktop.resourcebundle.ResourceBundleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

/**
 *
 */
public class ContactForm {
    private JPanel mainPanel;
    private JButton buttonOk;
    private JButton buttonCancel;
    private JTextArea textAreaTitle;
    private JScrollPane scrollPane;
    private JPanel contactPanel;
    private JPanel contactInfosPanel;
    private JPanel addressesPanel;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ResourceBundle generalResourceBundle = ResourceBundleFactory.getGeneralBundle();
    private final ResourceBundle messageResourceBundle = ResourceBundleFactory.getMessagesBundle();

    private Contact contact;

    public ContactForm() {
        super();
        setUI();
    }

    public ContactForm(Contact contact) {
        super();
        this.contact = contact;
        setUI();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Contact getContact() {
        refreshFromUI();
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        setUI();
    }

    protected void setUI() {

        // Event Listeners
        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPerformed_buttonOk_Click(e);
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPerformed_buttonCancel_Click(e);
            }
        });

        // UI initialization
        textAreaTitle.setOpaque(false);
        textAreaTitle.setText("NOVO");
        ((ContactInfosPanel)contactInfosPanel).setContactInfos(contact.getContactInfos());
        ((AddressesPanel)addressesPanel).setAddresses(contact.getAddresses());
        // ContactInfosPanel properties
//        contactInfosPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    /**
     * Custom creation of UI components.
     * Called in super's constructor.
     */
    private void createUIComponents() {
        contactInfosPanel = new ContactInfosPanel();
        addressesPanel = new AddressesPanel(false);
    }

    public void refreshFromUI() {
        ((ContactInfosPanel)contactInfosPanel).refreshFromUI();
        ((AddressesPanel)addressesPanel).refreshFromUI();
    }

    public void actionPerformed_buttonOk_Click(ActionEvent e) {
        RepositoryFactory.getEntityManager().getTransaction().begin();

        try {
            ContactRepository contactRepository = RepositoryFactory.getRepository(ContactRepository.class);

            Contact c = getContact();
            contactRepository.save(c);

            RepositoryFactory.getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
            logger.error("Error saving Contact.", ex);
            RepositoryFactory.getEntityManager().getTransaction().rollback();
        }

        JOptionPane.showConfirmDialog(this.mainPanel,
                messageResourceBundle.getString("ContactSavedOk"), "" , JOptionPane.PLAIN_MESSAGE);
    }

    public void actionPerformed_buttonCancel_Click(ActionEvent e) {

    }
}
