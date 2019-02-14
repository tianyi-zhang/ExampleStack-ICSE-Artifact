public class foo{
    public int read() throws IOException {
        switch(state)
        {
          case bol://read until non indentation character
            while(true){
                int ch = in.read();
                if (ch!=(int)' ' && ch!=(int)'\t'){
                    state = State.content;
                    return processChar(ch);
                }
            }

          case content: {
                int ch = in.read();
                return processChar(ch);
          }

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