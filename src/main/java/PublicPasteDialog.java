import com.github.lx200916.fishboneplugin.actions.PasteLifeTime;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.github.lx200916.fishboneplugin.actions.NewPasteKt.getPasteLifeTime;

public class PublicPasteDialog extends PasteDialog {
    private final String lang;
    private JPanel contentPane;
    private JButton cancelButton;
    private JButton OKButton;
    private JComboBox PasteLang;
    private JTextArea PasteContent;
    private JComboBox PasteType;
    private JCheckBox DeleteToken;

    public PublicPasteDialog(String pasteLang, final String pasteContent) {
        super();
//        setContentPane(contentPane);
        lang = pasteLang;
        setModal(true);
        setTitle("Create Public Paste");
        setSize(600, 600);
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setPasteContent(pasteContent);
        setPasteLang(pasteLang);
//        TextPrompt tp7 = new TextPrompt("First Name", tf7);

//        PasteID.setText("You can specify PasteID only in a Burn After Read Paste.");
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                listener.onCancelClicked(PublicPasteDialog.this);
//            }
//        });
//
//
//        cancelButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                listener.onCancelClicked(PublicPasteDialog.this);
//            }
//        });
//
//        cancelButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                listener.onCancelClicked(PublicPasteDialog.this);
//            }
//        });
//        OKButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (burnAfterRead.isSelected() && getPasteID().getText().length() != 0) {
//                    if (Pattern.matches("^[0-9a-z]{3,8}$", getPasteID().getText())) {
//                        listener.onOkClicked(PublicPasteDialog.this);
//                    } else {
//                        Messages.showMessageDialog("Sorry, PasteID should be 8-16 long with digits and letters.", "Error", Messages.getErrorIcon());
//                        return;
//                    }
//
//                } else {
//                    listener.onOkClicked(PublicPasteDialog.this);
//
//                }
//            }
//        });
//
        init();
    }

//    public JTextField getPasteID() {
//        return PasteID;
//    }

//    public void setPasteID(String pasteID) {
//        setPasteID(pasteID);
//    }

    @Override
    protected void doOKAction() {
        if (PasteContent.getText().isEmpty()){
            invalidContent();


        }else {
            super.doOKAction();
        }

    }
    @Override
    public boolean hasPasteToken() {
        return DeleteToken.isSelected();
    }
    @Override
    public PasteLifeTime getPasteType() {
        return getPasteLifeTime(PasteType.getSelectedIndex());
    }

    public String getPasteLang() {
        if (PasteLang.getSelectedItem() == null) {
            return lang;
        } else {
            System.out.println(PasteLang.getSelectedItem().toString());
//            Messages.showMessageDialog(PasteLang.getSelectedItem().toString(), "Error", Messages.getErrorIcon());

            return PasteLang.getSelectedItem().toString();

        }
    }

    public void setPasteLang(String pasteLang) {
//        PasteLang = pasteLang;
        PasteLang.getModel().setSelectedItem(pasteLang);
    }

//    public Boolean getBurnAfterRead() {
//        return burnAfterRead.isSelected();
//    }
//
//    public void setBurnAfterRead(Boolean burn) {
////        this.burnAfterRead = burnAfterRead;
//        burnAfterRead.setSelected(burn);
//    }

    public String getPasteContent() {
        return PasteContent.getText();
    }

    public void setPasteContent(String pasteContent) {
//        PasteContent = pasteContent;
        PasteContent.setText(pasteContent);

    }

    public void invalidContent() {
        Messages.showMessageDialog("Oops..no content..", "Empty Paste", Messages.getErrorIcon());

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }
}
