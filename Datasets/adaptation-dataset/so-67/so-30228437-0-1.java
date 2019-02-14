public class foo {
public int read() throws IOException {        
    int ch;
    switch(state){
        case bol: //beginning of line, read until non-indentation character
            while(true){
                ch = in.read();
                if (ch!=(int)' ' && ch!=(int)'\t'){
                    state = State.content;
                    return processChar(ch);
                }
            }

        case content:
            ch = in.read();
            return processChar(ch);

        //eol states replace all "\n" by "##\n"
        case eol1: 
            state = State.eol2;
            return (int)'#';

        case eol2:
            state = State.bol;
            return (int)'\n';

        case eof: 
            return -1;
    }
    return -1;
}
}