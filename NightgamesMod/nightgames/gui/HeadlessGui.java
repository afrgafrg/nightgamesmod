package nightgames.gui;

public class HeadlessGui extends GUI {
    private static final long serialVersionUID = 1L;

    public HeadlessGui() {
        super();
        this.setVisible(false);
    }
    
    @Override
    public void message(String msg) {
        
    }
    
}
