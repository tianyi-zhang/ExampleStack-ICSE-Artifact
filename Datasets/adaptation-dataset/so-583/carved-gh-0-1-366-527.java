public class foo{
    public static String reshape_reverse(String Str) {
        String Temp = " " + Str + "   ";
        char pre, at, post;
        StringBuilder reshapedString = new StringBuilder();
        int i = 0;
        int len = Str.length();

        char post_post;
        char pre_pre = ' ';

        while (i < len) {
            pre = Temp.charAt(i + 2);
            at = Temp.charAt(i + 1);
            post = Temp.charAt(i);

            int which_case = getCase(at);
            int what_case_post = getCase(post);
            int what_case_pre = getCase(pre);
            int what_case_post_post;
            int what_case_pre_pre;
            // which_case=0x000F&
            // Log.v("what case"," :" +which_case);
            int pre_step = 0;
            if (what_case_pre == TASHKEEL) {
                pre = Temp.charAt(i + 3);
                what_case_pre = getCase(pre);
            }
            if ((what_case_pre & LEFT_CHAR_MASK) == LEFT_CHAR_MASK) {
                pre_step = 1;

            }

            // System.out.println("##letter "+ pre);
            switch (which_case & 0x000F) {

                case NOTUSED_CHAR:
                case NOTARABIC_CHAR:

                    reshapedString.append(at);

                    i++;
                    continue;
                case NORIGHT_NOLEFT_CHAR:
                case TATWEEL_CHAR:
                    reshapedString.append(getShape(at, 0));

                    i++;
                    continue;
                case RIGHT_NOLEFT_CHAR_ALEF:

                    // System.out.println("--letter "+ pre);

                    if ((what_case_pre & 0x000F) == RIGHT_LEFT_CHAR_LAM) {
                        pre = Temp.charAt(i + 3);
                        // System.out.println("++letter "+ pre);
                        what_case_pre = getCase(pre);
                        pre_step = 0;
                        if ((what_case_pre & LEFT_CHAR_MASK) == LEFT_CHAR_MASK) {
                            pre_step = 1;

                        }
                        reshapedString.append(getShape(at, pre_step + 2));
                        i = i + 2;

                        continue;
                    } /*
                 * else if
                 * ((what_case_post&RIGHT_NOLEFT_CHAR_MASK)==RIGHT_NOLEFT_CHAR_MASK
                 * ){ reshapedString.append(getShape(at,2+pre_step)); i=i+1;
                 *
                 * continue;
                 *
                 *
                 * } else if (what_case_post==TANWEEN){
                 * reshapedString.append(getShape(at,pre_step)); i=i+1;
                 * continue;
                 *
                 *
                 * } else if (what_case_post==TASHKEEL){
                 * post_post=Temp.charAt(i+3);
                 * what_case_post_post=getCase(post_post); if
                 * ((what_case_post_post
                 * &RIGHT_NOLEFT_CHAR_MASK)==RIGHT_NOLEFT_CHAR_MASK){
                 * reshapedString.append(getShape(at,2+pre_step)); i=i+1;
                 *
                 * continue;
                 *
                 * } else { reshapedString.append(getShape(at,pre_step)); i=i+1;
                 * continue;
                 *
                 * }
                 *
                 *
                 *
                 *
                 *
                 * }
                 */else {
                        reshapedString.append(getShape(at, pre_step));
                        i = i + 1;
                        continue;

                    }
                case RIGHT_LEFT_CHAR_LAM:
                case RIGHT_LEFT_CHAR:
                    if ((what_case_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                        reshapedString.append(getShape(at, 2 + pre_step));
                        i = i + 1;
                        continue;

                    } else if (what_case_post == TANWEEN) {
                        reshapedString.append(getShape(at, pre_step));
                        i = i + 1;
                        continue;

                    } else if (what_case_post == TASHKEEL) {
                        post_post = Temp.charAt(i + 3);
                        what_case_post_post = getCase(post_post);
                        if ((what_case_post_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
                            reshapedString.append(getShape(at, 2 + pre_step));
                            i = i + 1;
                            continue;

                        } else {
                            reshapedString.append(getShape(at, pre_step));
                            i = i + 1;
                            continue;

                        }

                    } else {
                        reshapedString.append(getShape(at, pre_step));
                        i = i + 1;
                        continue;

                    }

                case RIGHT_NOLEFT_CHAR:
                    reshapedString.append(getShape(at, pre_step));
                    i = i + 1;
                    continue;
                case TASHKEEL:
                    reshapedString.append(getShape(at, 0));
                    i++;
                    pre_pre = pre;
                    continue;
                case TANWEEN:
                    reshapedString.append(getShape(at, 0));
                    i++;
                    pre_pre = pre;
                    continue;

                default:
                    reshapedString.append(getShape(at, 0));
                    i++;

            }

        }

        return reshapedString.toString();
    }
}