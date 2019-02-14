public class foo{
    public View create( Element elem )
    {
        String kind = elem.getName();
        if ( kind != null )
        {
            switch ( kind )
            {
                case AbstractDocument.ContentElementName:
                    return new WrapLabelView( elem );
                case AbstractDocument.ParagraphElementName:
                    return new ParagraphView( elem );
                case AbstractDocument.SectionElementName:
                    return new BoxView( elem, View.Y_AXIS );
                case StyleConstants.ComponentElementName:
                    return new ComponentView( elem );
                case StyleConstants.IconElementName:
                    return new IconView( elem );
            }
        }

        // default to text display
        return new LabelView( elem );
    }
}