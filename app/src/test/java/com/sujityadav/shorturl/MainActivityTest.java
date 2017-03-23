package com.sujityadav.shorturl;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

/**
 * Created by sujit yadav on 3/24/2017.
 */
public class MainActivityTest {
    @Test
    public void getfromCache() throws Exception {
        SharedPreferences mockedSharedPreference = Mockito.mock(SharedPreferences.class);
        Context context = Mockito.mock(Context.class);
        MainActivity mainActivity= new MainActivity();
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(mockedSharedPreference);
        Mockito.when(mockedSharedPreference.getString(Mockito.anyString(), Mockito.anyString())).thenReturn("foobar");
        Assert.assertEquals("foobar", mainActivity.getfromCache("foobar",context));
    }



}