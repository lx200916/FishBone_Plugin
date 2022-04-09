import com.github.lx200916.fishboneplugin.actions.PasteLifeTime;
import com.intellij.openapi.ui.DialogWrapper;


public abstract class PasteDialog extends DialogWrapper {


    protected PasteDialog() {
        super(true);
    }
    public boolean isPrivate=false;
    public abstract String getPasteContent();
    public abstract String getPasteLang();
    public  String getPastePass(){
        return "";
    };

    public abstract PasteLifeTime getPasteType();
    public abstract boolean hasPasteToken();


    }
