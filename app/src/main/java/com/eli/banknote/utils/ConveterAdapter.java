package com.eli.banknote.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.eli.banknote.utils.Constants.DATE_FORMAT_PATTERN;

public class ConveterAdapter {
    Context context;
    Locale loc;

    public ConveterAdapter(Context context){
        this.context = context;
    }

    public String convertToLocalLanguage(String lng)
    {
        loc = new Locale(lng);
        return loc.getDisplayLanguage(loc);
    }
    public String getLocaleCountryName(String countryName){
        loc = new Locale("", countryName);
        return loc.getISO3Country();
    }

    public String convertCountryNameIntoISO(String countryname){
        loc = new  Locale(countryname);
        return loc.getISO3Country();

    }
    public String convertToLocaltime(String GMTString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a");
// Adjust locale and zone appropriately

        Date date = inputFormat.parse(GMTString);
        return  outputFormat.format(date);
    }
}
