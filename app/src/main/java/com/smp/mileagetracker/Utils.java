package com.smp.mileagetracker;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Steve on 1/11/15.
 */
public class Utils
{
    public static synchronized Object readObjectFromFile(Context context, String fileName)
    {
        Object result = null;
        FileInputStream fis = null;
        try
        {
            fis = context.openFileInput(fileName);
            ObjectInputStream objectIn = new ObjectInputStream(fis);
            return objectIn.readObject();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally
        {
            if (fis != null)
                try
                {
                    fis.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public static synchronized void writeObjectToFile(Context context, String fileName, Object obj)
    {
        FileOutputStream fos = null;
        try
        {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOut = new ObjectOutputStream(fos);
            objectOut.writeObject(obj);
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                fos.flush();
                fos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static final String LOG_FILE_NAME = "LOGFILE";

    @SuppressWarnings("unchecked")
    public static ArrayList<TrackInfoComplete> getTrackingList(Context context)
    {
        ArrayList<TrackInfoComplete> log;

        Object obj = readObjectFromFile(context, LOG_FILE_NAME);
        if (obj != null)
            log = (ArrayList<TrackInfoComplete>) obj;
        else
            log = new ArrayList<>();

        return log;

    }
}
