package UI.ComboBox.Items;

public abstract class ComboBoxItemBase 
{
    private String key;

    public ComboBoxItemBase(String key)
    {
        this.key = key;
    }

    @Override
    public String toString()
    {
        return key;
    }

    public String getKey()
    {
        return key;
    }
}