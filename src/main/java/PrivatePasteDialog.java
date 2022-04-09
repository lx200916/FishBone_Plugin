import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBPasswordField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.github.lx200916.fishboneplugin.actions.PasteLifeTime;

import static com.github.lx200916.fishboneplugin.actions.NewPasteKt.getPasteLifeTime;

public class PrivatePasteDialog extends PasteDialog {
    private final String lang;
    public boolean isPrivate=true;

    private JButton cancelButton;
    private JButton OKButton;
//    private JTextField PasteID;
    private JBPasswordField PastePass;
    private JButton button3;
    private JComboBox PasteLang;
    private JLabel languageLabel;
    private JTextArea PasteContent;
    private JLabel contentLabel;
    private JPanel contentPanel;
//    private JCheckBox burnAfterRead;
    private JComboBox PasteType;
    private JCheckBox DeleteToken;


    public PrivatePasteDialog(String pasteLang, final String pasteContent, String password) {
        super();
//        setContentPane(contentPanel);
        setModal(true);
        setTitle("Create Private Paste");


        setSize(600, 600);

        PastePass.setText(password);
        setPasteContent(pasteContent);
        setPasteLang(pasteLang);
        System.out.println(PastePass.getEchoChar());
        lang = pasteLang;
//        PasteID.setText("You can specify PasteID only in a Burn After Read Paste.");
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                listener.onCancelClicked(PrivatePasteDialog.this);
//
//            }
//        });


//        cancelButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                listener.onCancelClicked(PrivatePasteDialog.this);
//            }
//        });
//
//        cancelButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                listener.onCancelClicked(PrivatePasteDialog.this);
//            }
//        });
//        OKButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//
////        burnAfterRead.addChangeListener(new ChangeListener() {
////            @Override
////            public void stateChanged(ChangeEvent e) {
////                JCheckBox checkBox = (JCheckBox) e.getSource();
////                Boolean state = checkBox.getModel().isSelected();
////
////
//            }
//        });
        button3.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
//                super.mousePressed(e);
                PastePass.setEchoChar((char)0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
//                super.mouseReleased(e);
                PastePass.setEchoChar('*');

            }

//            @Override
//            public void mouseClicked(MouseEvent e) {
////                super.mouseClicked(e);
//                System.out.println(PastePass.getEchoChar());
//                if(PastePass.getEchoChar()=='*'){
//                    PastePass.setEchoChar((char)0);
//                }else {
//                    PastePass.setEchoChar('*');
//
//                }
//            }

        });
        init();
    }


    public String getPasteContent() {
        return PasteContent.getText();
    }

    public void setPasteContent(String pasteContent) {
//        PasteContent = pasteContent;
        PasteContent.setText(pasteContent);

    }

//    public Boolean getBurnAfterRead() {
//        return burnAfterRead.isSelected();
//    }
//
//    public void setBurnAfterRead(Boolean burn) {
////        this.burnAfterRead = burnAfterRead;
//        burnAfterRead.setSelected(burn);
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
public void invalidContent() {
        Messages.showMessageDialog("Oops..no content..", "Empty Paste", Messages.getErrorIcon());

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

    public String getPastePass() {
        return new String(PastePass.getPassword());
    }

    @Override
    public PasteLifeTime getPasteType() {
        return getPasteLifeTime(PasteType.getSelectedIndex());
    }

    @Override
    public boolean hasPasteToken() {
        return DeleteToken.isSelected();
    }

    public void invalidPasteid() {
        Messages.showMessageDialog("Oops..PasteID has been Taken..", "PasteID Taken", new ImageIcon("/icon/ohno.gif"));

    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPanel;
    }
}
