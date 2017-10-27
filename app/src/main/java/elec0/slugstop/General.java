package elec0.slugstop;

import android.content.Context;
import android.content.Intent;

public class General
{
    /***
     * Start a new intent with the current context and class.
     * @param c
     * @param cl
     */
    public static void startIntent(Context c, Class<?> cl)
    {
        Intent intent = new Intent(c, cl);
        intent.setFlags(intent.getFlags());
        c.startActivity(intent);
    }
}
