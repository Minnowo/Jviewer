package nyaa.alice.jviewer.ui.components;

public class ComboBoxItemInt extends ComboBoxItemBase
{
    private int value;

    public ComboBoxItemInt(String key, int value)
    {
        super(key);
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}
