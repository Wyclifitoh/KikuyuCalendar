package com.kyssanet.kikuyucalendar;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.gjiazhe.scrollparallaximageview.ScrollParallaxImageView;
import com.gjiazhe.scrollparallaximageview.parallaxstyle.VerticalMovingStyle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.kyssanet.kikuyucalendar.weekview.DateTimeInterpreter;
import com.kyssanet.kikuyucalendar.weekview.MonthLoader;
import com.kyssanet.kikuyucalendar.weekview.WeekView;
import com.kyssanet.kikuyucalendar.weekview.WeekViewEvent;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AppBarTracking,
        WeekView.EventClickListener,
        MonthLoader.MonthChangeListener,
        WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener,WeekView.ScrollListener{

    private static final String TAG = "MainActivity";
    public static LocalDate lastdate = LocalDate.now();
    private String daysList[] = {"", "KERI", "GATATU", "KANA",
            "GATANO", "GATANDATU", "MUTUA RURU", "KIUMBA"};
    public static int topspace = 0;
    private View myshadow;
    long lasttime;
    int mycolor;
    MyRecyclerVIew mNestedView;
    private ViewPager monthviewpager;
    private HashMap<LocalDate, EventInfo> alleventlist;
    private DrawerLayout drawerLayout;
    private int mAppBarOffset = 0;
    private boolean mAppBarIdle = true;
    private int mAppBarMaxOffset = 0;
    private View shadow;
    private AppBarLayout mAppBar;
    private boolean mIsExpanded = false;
    private View redlay;
    private ImageView mArrowImageView;
    private TextView monthname;
    private Toolbar toolbar;
    private int lastchangeindex = -1;
    private boolean isappbarclosed = true;
    private int month;
    private int expandedfirst;
    private View roundrect;
    private TextView eventnametextview, eventrangetextview, holidaytextview,eventfixstextview;
    private ImageView calendaricon;
    private View eventview, fullview;
    private GooglecalenderView calendarView;
    private ArrayList<EventModel> eventalllist;
    private boolean isgivepermission;
    private HashMap<LocalDate, Integer> indextrack;
    private ImageButton closebtn;
    private HashMap<LocalDate, Integer> dupindextrack;
    View weekviewcontainer;
    private String[] var = {"Kerĩ", "Gatatũ", "Kana", "Gatano", "Gatandatũ", "Mũtua Rũru", "Kiumia"};
    public static String monthNameEnglish = "BAZUUU";
    private int[] monthresource = {
            R.drawable.bkg_01_jan,
            R.drawable.bkg_02_feb,
            R.drawable.bkg_03_mar,
            R.drawable.bkg_04_apr,
            R.drawable.bkg_05_may,
            R.drawable.bkg_06_jun,
            R.drawable.bkg_07_jul,
            R.drawable.bkg_08_aug,
            R.drawable.bkg_09_sep,
            R.drawable.bkg_10_oct,
            R.drawable.bkg_11_nov,
            R.drawable.bkg_12_dec
    };
     WeekView mWeekView;

    public static void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }

    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(false);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().setNavigationBarColor(Color.parseColor("#f1f3f5"));
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home){

            drawerLayout.openDrawer(Gravity.LEFT);
                return true;
        }
        if (item.getItemId() == R.id.action_favorite) {
            final LocalDate localDate = LocalDate.now();
            if (monthviewpager.getVisibility() == View.VISIBLE && monthviewpager.getAdapter() != null) {
                monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(localDate), false);
            }
            if (weekviewcontainer.getVisibility()==View.VISIBLE){
                Calendar todaydate=Calendar.getInstance();
                todaydate.set(Calendar.DAY_OF_MONTH,localDate.getDayOfMonth());
                todaydate.set(Calendar.MONTH,localDate.getMonthOfYear()-1);
                todaydate.set(Calendar.YEAR,localDate.getYear());
                mWeekView.goToDate(todaydate);

            }
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
            mNestedView.stopScroll();
            if (indextrack.containsKey(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()))) {

                final Integer val = indextrack.get(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));

                if (isAppBarExpanded()) {
                    calendarView.setCurrentmonth(new LocalDate());

                    expandedfirst = val;
                    topspace = 20;
                    linearLayoutManager.scrollToPositionWithOffset(val, 20);
                    EventBus.getDefault().post(new MonthChange(localDate, 0));
                    month = localDate.getDayOfMonth();
                    lastdate = localDate;
                } else {

//                   calendarView.setCurrentmonth(new LocalDate());
//                   calendarView.adjustheight();
//                   mIsExpanded = false;
//                   mAppBar.setExpanded(false, false);
                    expandedfirst = val;
                    topspace = 20;
                    linearLayoutManager.scrollToPositionWithOffset(val, 20);
                    EventBus.getDefault().post(new MonthChange(localDate, 0));
                    month = localDate.getDayOfMonth();
                    lastdate = localDate;

                }


            }

        }
        return super.onOptionsItemSelected(item);

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = ResourcesCompat.getFont(this, R.font.googlesansmed);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public int getnavigationHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void setMargins(View view, int left, int top, int right, int bottom, int width, int height) {

        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            view.setLayoutParams(new CoordinatorLayout.LayoutParams(width, height));
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void closebtnClick() {
        closebtn.setVisibility(View.GONE);
        eventnametextview.setVisibility(View.GONE);
        roundrect.setVisibility(View.GONE);
        eventrangetextview.setVisibility(View.GONE);
        calendaricon.setVisibility(View.GONE);
        holidaytextview.setVisibility(View.GONE);
        eventfixstextview.setVisibility(View.GONE);
        ValueAnimator animwidth = ValueAnimator.ofInt(getDevicewidth(), eventview.getWidth());
        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.width = val;
                redlay.setLayoutParams(layoutParams);
            }
        });
        animwidth.setDuration(300);

        ValueAnimator animheight = ValueAnimator.ofInt(getDeviceHeight(), 0);
        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.height = val;
                redlay.setLayoutParams(layoutParams);
                if (redlay.getTranslationZ() != 0 && valueAnimator.getAnimatedFraction() > 0.7) {
                    GradientDrawable shape =  new GradientDrawable();
                    shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
                    shape.setColor(mycolor);
                    redlay.setBackground(shape);
                    redlay.setTranslationZ(0);
                    shadow.setVisibility(View.GONE);
                }
            }
        });
        animheight.setDuration(300);

        ValueAnimator animx = ValueAnimator.ofFloat(0, eventview.getLeft());
        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                Float val = (Float) valueAnimator.getAnimatedValue();

                redlay.setTranslationX(val);
            }
        });
        animx.setDuration(300);

        ValueAnimator animy = ValueAnimator.ofFloat(0, fullview.getTop() + toolbar.getHeight());

        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                redlay.setTranslationY(val);
            }
        });
        animy.setDuration(300);
        animwidth.start();
        animheight.start();
        animy.start();
        animx.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Locale locale = new Locale("ki");
//        Locale.setDefault(locale);
//        Configuration config = getBaseContext().getResources().getConfiguration();
//        config.locale = locale;
//        getBaseContext().getResources().updateConfiguration(config,
//                getBaseContext().getResources().getDisplayMetrics());
        mWeekView = (WeekView) findViewById(R.id.weekView);
        weekviewcontainer=findViewById(R.id.weekViewcontainer);
       drawerLayout=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.navigation_view);
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.Day){
                    weekviewcontainer.setVisibility(View.VISIBLE);
                    monthviewpager.setVisibility(View.GONE);
                    mNestedView.setVisibility(View.GONE);
                    mWeekView.setNumberOfVisibleDays(1);
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, getResources().getDisplayMetrics()));
                        Calendar todaydate=Calendar.getInstance();
                        todaydate.set(Calendar.DAY_OF_MONTH,MainActivity.lastdate.getDayOfMonth());
                        todaydate.set(Calendar.MONTH,MainActivity.lastdate.getMonthOfYear()-1);
                        todaydate.set(Calendar.YEAR,MainActivity.lastdate.getYear());
                        mWeekView.goToDate(todaydate);
//                    Log.d(TAG, "onNavigationItemSelected: DATAAAA "+);
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                    ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                    mAppBar.setElevation(0);
                    mArrowImageView.setVisibility(View.VISIBLE);
                        drawerLayout.closeDrawer(Gravity.LEFT);
                }
               else if (item.getItemId()==R.id.Week){
                    weekviewcontainer.setVisibility(View.VISIBLE);
                    monthviewpager.setVisibility(View.GONE);
                    mNestedView.setVisibility(View.GONE);
                    mWeekView.setNumberOfVisibleDays(7);
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                    Calendar todaydate=Calendar.getInstance();
                    todaydate.set(Calendar.DAY_OF_MONTH,MainActivity.lastdate.getDayOfMonth());
                    todaydate.set(Calendar.MONTH,MainActivity.lastdate.getMonthOfYear()-1);
                    todaydate.set(Calendar.YEAR,MainActivity.lastdate.getYear());
                    mWeekView.goToDate(todaydate);
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                    ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                    mAppBar.setElevation(0);
                    mArrowImageView.setVisibility(View.VISIBLE);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                else if (item.getItemId()==R.id.threeday){
                    weekviewcontainer.setVisibility(View.VISIBLE);
                    monthviewpager.setVisibility(View.GONE);
                    mNestedView.setVisibility(View.GONE);
                    mWeekView.setNumberOfVisibleDays(3);
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                    Calendar todaydate=Calendar.getInstance();
                    todaydate.set(Calendar.DAY_OF_MONTH,MainActivity.lastdate.getDayOfMonth());
                    todaydate.set(Calendar.MONTH,MainActivity.lastdate.getMonthOfYear()-1);
                    todaydate.set(Calendar.YEAR,MainActivity.lastdate.getYear());
                    mWeekView.goToDate(todaydate);
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                    ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                    mAppBar.setElevation(0);
                    mArrowImageView.setVisibility(View.VISIBLE);
                    drawerLayout.closeDrawer(Gravity.LEFT);

                }
                else if (item.getItemId()==R.id.monthviewitem){
                    mAppBar.setExpanded(false, false);
                    mNestedView.setVisibility(View.GONE);
                    weekviewcontainer.setVisibility(View.GONE);
                    monthviewpager.setVisibility(View.VISIBLE);
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                    ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(false);
                    mAppBar.setElevation(0);
                    mArrowImageView.setVisibility(View.INVISIBLE);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(MainActivity.lastdate), true);

                }
                else {
                    LocalDate localDate = new LocalDate();
                    String yearstr = MainActivity.lastdate.getYear() == localDate.getYear() ? "" : MainActivity.lastdate.getYear() + "";
                  //  monthname.setText(MainActivity.lastdate.toString("MMMM") + " " + yearstr);
                    String transYear = MainActivity.lastdate.toString("MMMM") + " " + yearstr;
                    if (transYear.contains("January")) {
                        monthNameEnglish = "January "+yearstr;
                        monthname.setText("MŨGAA "+yearstr);
                    }
                    if (transYear.contains("February")) {
                        monthNameEnglish = "February "+yearstr;
                        monthname.setText("MŨGETHO "+yearstr);
                    }
                    if (transYear.contains("March")) {
                        monthNameEnglish = "March "+yearstr;
                        monthname.setText("KĨHU " +yearstr);
                    }
                    if (transYear.contains("April")) {
                        monthNameEnglish = "April "+yearstr;
                        monthname.setText("MŨTHATŨ "+yearstr);
                    }
                    if (transYear.contains("May")) {
                        monthNameEnglish = "May "+yearstr;
                        monthname.setText("MŨGIRA NJARA "+yearstr);
                    }
                    if (transYear.contains("June")) {
                        monthNameEnglish = "June "+yearstr;
                        monthname.setText("GATHATHANWA "+yearstr);
                    }
                    if (transYear.contains("July")) {
                        monthNameEnglish = "July "+yearstr;
                        monthname.setText("GATHANO "+yearstr);
                    }
                    if (transYear.contains("August")) {
                        monthNameEnglish = "August "+yearstr;
                        monthname.setText("MUORIA NYONI "+yearstr);
                    }
                    if (transYear.contains("September")) {
                        monthNameEnglish = "September "+yearstr;
                        monthname.setText("MŨGAA WA KEERĨ "+yearstr);
                    }
                    if (transYear.contains("October")) {
                        monthNameEnglish = "October "+yearstr;
                        monthname.setText("MWANIA THENGE "+yearstr);
                    }
                    if (transYear.contains("November")) {
                        monthNameEnglish = "November "+yearstr;
                        monthname.setText("KANYUA HŨNGŨ "+yearstr);
                    }
                    if (transYear.contains("December")) {
                        monthNameEnglish = "December "+yearstr;
                        monthname.setText("GATUMU "+yearstr);
                    }
                    Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEEEEEEAAAAAAAAAARRR "+ yearstr);
                    Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEAR "+ MainActivity.lastdate.toString("MMMM") + " " + yearstr);
                    calendarView.setCurrentmonth(MainActivity.lastdate);
                    calendarView.adjustheight();
                    mIsExpanded = false;
                    mAppBar.setExpanded(false, false);
                    EventBus.getDefault().post(new MessageEvent(MainActivity.lastdate));
                    monthviewpager.setVisibility(View.GONE);
                    weekviewcontainer.setVisibility(View.GONE);
                    mNestedView.setVisibility(View.VISIBLE);
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                    ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                    mAppBar.setElevation(20);
                    mArrowImageView.setVisibility(View.VISIBLE);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                item.setChecked(true);
                return true;
            }

        });

        eventalllist = new ArrayList<>();
        indextrack = new HashMap<>();
        dupindextrack = new HashMap<>();
        mAppBar = findViewById(R.id.app_bar);
        redlay = findViewById(R.id.redlay);
        redlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        shadow = findViewById(R.id.shadow);
        closebtn = findViewById(R.id.closebtn);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vh) {
                closebtnClick();
            }
        });
        roundrect = findViewById(R.id.roundrect);
        eventnametextview = findViewById(R.id.textView12);
        eventrangetextview = findViewById(R.id.textView13);
        calendaricon = findViewById(R.id.imageView2);
        holidaytextview = findViewById(R.id.textView14);
        eventfixstextview=findViewById(R.id.textView014);
        calendarView = findViewById(R.id.calander);




        calendarView.setPadding(0, getStatusBarHeight(), 0, 0);
        mNestedView = findViewById(R.id.nestedView);
        monthviewpager = findViewById(R.id.monthviewpager);
        monthviewpager.setOffscreenPageLimit(1);
//        monthviewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                if (monthviewpager.getVisibility()==View.GONE)return;
//                if (isAppBarClosed()){
//                    Log.e("selected",position+"");
//                    calendarView.setCurrentmonth(position);
//                     calendarView.adjustheight();
//                    mIsExpanded=false;
//                    mAppBar.setExpanded(false,false);
//                    //  myPagerAdapter.getFirstFragments().get(position).updategrid();
//                    LocalDate localDate=new LocalDate();
//                    MonthViewPagerAdapter monthPageAdapter = (MonthViewPagerAdapter) monthviewpager.getAdapter();
//                    MonthModel monthModel = monthPageAdapter.monthModels.get(position);
//                    String year=monthModel.getYear()==localDate.getYear()?"":monthModel.getYear()+"";
//                    monthname.setText(monthModel.getMonthnamestr()+" "+year);
//                    EventBus.getDefault().post(new MessageEvent(new LocalDate(monthModel.getYear(),monthModel.getMonth(),1)));
//
//                    // if (monthChangeListner!=null)monthChangeListner.onmonthChange(myPagerAdapter.monthModels.get(position));
//                }
//                else {
//                    calendarView.setCurrentmonth(position);
//                }
//            }
//        });

        monthviewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

                if (monthviewpager.getVisibility() == View.GONE) return;
                if (isAppBarClosed()) {
                    Log.e("selected", i + "");
//                    calendarView.setCurrentmonth(i);
//                    calendarView.adjustheight();
//                    mIsExpanded=false;
//                    mAppBar.setExpanded(false,false);
                    //EventBus.getDefault().post(new MessageEvent(new LocalDate(myPagerAdapter.monthModels.get(position).getYear(),myPagerAdapter.monthModels.get(position).getMonth(),1)));
                    //  myPagerAdapter.getFirstFragments().get(position).updategrid();
                    LocalDate localDate = new LocalDate();
                    MonthPageAdapter monthPageAdapter = (MonthPageAdapter) monthviewpager.getAdapter();
                    MonthModel monthModel = monthPageAdapter.getMonthModels().get(i);
                    String year = monthModel.getYear() == localDate.getYear() ? "" : monthModel.getYear() + "";
                    //monthname.setText(monthModel.getMonthnamestr() + " " + year);
                    MainActivity.lastdate=new LocalDate(monthModel.getYear(),monthModel.getMonth(),1);
                    String transYear = monthModel.getMonthnamestr() + " " + year;
                    if (transYear.contains("January")) {
                        monthNameEnglish = "January "+year;
                        monthname.setText("MŨGAA "+year);
                    }
                    if (transYear.contains("February")) {
                        monthNameEnglish = "February "+year;
                        monthname.setText("MŨGETHO "+year);
                    }
                    if (transYear.contains("March")) {
                        monthNameEnglish = "March "+year;
                        monthname.setText("KĨHU "+year);
                    }
                    if (transYear.contains("April")) {
                        monthNameEnglish = "April "+year;
                        monthname.setText("MŨTHATŨ "+year);
                    }
                    if (transYear.contains("May")) {
                        monthNameEnglish = "May "+year;
                        monthname.setText("MŨGIRA NJARA "+year);
                    }
                    if (transYear.contains("June")) {
                        monthNameEnglish = "June "+year;
                        monthname.setText("GATHATHANWA "+year);
                    }
                    if (transYear.contains("July")) {
                        monthNameEnglish = "July "+year;
                        monthname.setText("GATHANO "+year);
                    }
                    if (transYear.contains("August")) {
                        monthNameEnglish = "August "+year;
                        monthname.setText("MUORIA NYONI "+year);
                    }
                    if (transYear.contains("September")) {
                        monthNameEnglish = "September "+year;
                        monthname.setText("MŨGAA WA KEERĨ "+year);
                    }
                    if (transYear.contains("October")) {
                        monthNameEnglish = "October "+year;
                        monthname.setText("MWANIA THENGE "+year);
                    }
                    if (transYear.contains("November")) {
                        monthNameEnglish = "November "+year;
                        monthname.setText("KANYUA HŨNGŨ "+year);
                    }
                    if (transYear.contains("December")) {
                        monthNameEnglish = "December "+year;
                        monthname.setText("GATUMU "+year);
                    }
                    Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEEEEEEAAAAAAAAAARRR "+ year);
                    Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEAR3333 "+ lastdate);
                    // EventBus.getDefault().post(new MessageEvent(new LocalDate(monthModel.getYear(),monthModel.getMonth(),1)));
                    // if (monthChangeListner!=null)monthChangeListner.onmonthChange(myPagerAdapter.monthModels.get(position));
                } else {
                    // calendarView.setCurrentmonth(i);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        //  setMargins(mNestedView,0,0,0,getnavigationHeight());
        mNestedView.setAppBarTracking(this);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNestedView.setLayoutManager(linearLayoutManager);
        DateAdapter dateAdapter = new DateAdapter();
        mNestedView.setAdapter(dateAdapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(dateAdapter);
        mNestedView.addItemDecoration(headersDecor);
        EventBus.getDefault().register(this);

        monthname = findViewById(R.id.monthname);
        calendarView.setMonthChangeListner(new MonthChangeListner() {
            @Override
            public void onmonthChange(MonthModel monthModel) {
                /**
                 * call when Googlecalendarview is open  scroll viewpager available inside GoogleCalendar
                 */
                LocalDate localDate = new LocalDate();
                String year = monthModel.getYear() == localDate.getYear() ? "" : monthModel.getYear() + "";
                //.setText(monthModel.getMonthnamestr() + " " + year);
                // second translation here['
                String transYear = monthModel.getMonthnamestr() + " " + year;
                if (transYear.contains("January")) {
                    monthNameEnglish = "January "+year;
                    monthname.setText("MŨGAA "+ year);
                }
                if (transYear.contains("February")) {
                    monthNameEnglish = "February "+year;
                    monthname.setText("MŨGETHO  "+ year);
                }
                if (transYear.contains("March")) {
                    monthNameEnglish = "March "+year;
                    monthname.setText("KĨHU "+ year);
                }
                if (transYear.contains("April")) {
                    monthNameEnglish = "April "+year;
                    monthname.setText("MŨTHATŨ "+ year);
                }
                if (transYear.contains("May")) {
                    monthNameEnglish = "May "+year;
                    monthname.setText("MŨGIRA NJARA "+ year);
                }
                if (transYear.contains("June")) {
                    monthNameEnglish = "June "+year;
                    monthname.setText("GATHATHANWA "+ year);
                }
                if (transYear.contains("July")) {
                    monthNameEnglish = "July "+year;
                    monthname.setText("GATHANO "+ year);
                }
                if (transYear.contains("August")) {
                    monthNameEnglish = "August "+year;
                    monthname.setText("MUORIA NYONI  "+ year);
                }
                if (transYear.contains("September")) {
                    monthNameEnglish = "September "+year;
                    monthname.setText("MŨGAA WA KEERĨ  "+ year);
                }
                if (transYear.contains("October")) {
                    monthNameEnglish = "October "+year;
                    monthname.setText("MWANIA THENGE "+ year);
                }
                if (transYear.contains("November")) {
                    monthNameEnglish = "November "+year;
                    monthname.setText("KANYUA HŨNGŨ  "+ year);
                }
                if (transYear.contains("December")) {
                    monthNameEnglish = "December "+year;
                    monthname.setText("GATUMU "+ year);
                }
                Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEEEEEEAAAAAAAAAARRR "+ year);
                Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEAR2222r "+ monthModel.getMonthnamestr() + " " + year);
                if (weekviewcontainer.getVisibility()==View.VISIBLE){
                    Calendar todaydate=Calendar.getInstance();
                    todaydate.set(Calendar.DAY_OF_MONTH,1);
                    todaydate.set(Calendar.MONTH,monthModel.getMonth()-1);
                    todaydate.set(Calendar.YEAR,monthModel.getYear());
                    mWeekView.goToDate(todaydate);

                }
//                if (monthviewpager.getVisibility()== View.VISIBLE&&monthviewpager.getAdapter()!=null){
//                    monthviewpager.setCurrentItem(calendarView.getCurrentmonth(),false);
//                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 200);
            }
        } else {
            isgivepermission=true;
            LocalDate mintime = new LocalDate().minusYears(5);
            LocalDate maxtime = new LocalDate().plusYears(5);
            alleventlist = Utility.readCalendarEvent(this, mintime, maxtime);
            calendarView.init(alleventlist, mintime, maxtime);
            calendarView.setCurrentmonth(new LocalDate());
            calendarView.adjustheight();
            mIsExpanded = false;
            mAppBar.setExpanded(false, false);

        }
        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
//        expandCollapse = findViewById(R.id.expandCollapseButton);
        mArrowImageView = findViewById(R.id.arrowImageView);
        if (monthviewpager.getVisibility() == View.VISIBLE) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(false);
            mAppBar.setElevation(0);
            mArrowImageView.setVisibility(View.INVISIBLE);
        } else {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
            mAppBar.setElevation(20);
            mArrowImageView.setVisibility(View.VISIBLE);
        }

        mNestedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            LinearLayoutManager llm = (LinearLayoutManager) mNestedView.getLayoutManager();
            DateAdapter dateAdapter = (DateAdapter) mNestedView.getAdapter();
            int mydy;
            private int offset = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (mAppBarOffset != 0 && isappbarclosed && newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    calendarView.setCurrentmonth(dateAdapter.geteventallList().get(expandedfirst).getLocalDate());
                    calendarView.adjustheight();
                    mIsExpanded = false;
                    mAppBar.setExpanded(false, false);
                    Log.e("callme", "statechange");

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isappbarclosed) {

                    int pos = llm.findFirstVisibleItemPosition();
                    View view = llm.findViewByPosition(pos);

                    int currentmonth = dateAdapter.geteventallList().get(pos).getLocalDate().getMonthOfYear();

                    if (dateAdapter.geteventallList().get(pos).getType() == 1) {


                        if (dy > 0 && Math.abs(view.getTop()) > 100) {
                            if (month != currentmonth)
                                EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate(), dy));
                            month = currentmonth;
                            lastdate = dateAdapter.geteventallList().get(pos).getLocalDate();
                            expandedfirst = pos;
                        } else if (dy < 0 && Math.abs(view.getTop()) < 100 && pos - 1 > 0) {


                            pos--;
                            currentmonth = dateAdapter.geteventallList().get(pos).getLocalDate().getMonthOfYear();


                            if (month != currentmonth)
                                EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate(), dy));
                            month = currentmonth;
                            lastdate = dateAdapter.geteventallList().get(pos).getLocalDate().dayOfMonth().withMaximumValue();
                            expandedfirst = pos;
                        }
//                       if (dy>=0){
//                           if (Math.abs(view.getTop())>100){
//                               offset=0;
//                               mydy=dy;
//                              // calendarView.setCurrentmonth(dateAdapter.geteventallList().get(pos).getLocalDate());
//                               if (month!=currentmonth)EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate()));
//                               month=currentmonth;
//
//                           }
//                           else {
//                               if (pos-1>0)firstitem=pos-1;
//                               lastdate=lastdate.minusDays(1);
//                           }
//                       }
//                       else if (dy<0){
//                            Log.e("viewtop",view.getTop()+"");
//                           if (Math.abs(view.getTop())<10){
//                               offset=0;
//                               mydy=dy;
//                              // calendarView.setCurrentmonth(dateAdapter.geteventallList().get(pos).getLocalDate());
//                               if (month!=currentmonth)EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate()));
//                               month=currentmonth;
//                           }
//                           else {
//                               if (pos+1<dateAdapter.getItemCount())firstitem=pos+1;
//
//                               lastdate=lastdate.plusDays(1);
//                           }
//                       }


                    } else {
                        lastdate = dateAdapter.geteventallList().get(pos).getLocalDate();
                        expandedfirst = pos;
                    }

                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });


        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        }


        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {


                if (mAppBarOffset != verticalOffset) {
                    mAppBarOffset = verticalOffset;
                    mAppBarMaxOffset = -mAppBar.getTotalScrollRange();
                    //calendarView.setTranslationY(mAppBarOffset);
                    //calendarView.setLayoutParams(new CollapsingToolbarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,500));
                    int totalScrollRange = appBarLayout.getTotalScrollRange();
                    float progress = (float) (-verticalOffset) / (float) totalScrollRange;
                    if (monthviewpager.getVisibility()==View.GONE&&mNestedView.getVisibility()==View.VISIBLE)mAppBar.setElevation(20+(20*Math.abs(1-progress)));
                    if (weekviewcontainer.getVisibility()==View.VISIBLE){
                        mAppBar.setElevation(20-(20*Math.abs(progress)));


                    }
                    if (Math.abs(progress)>0.45){
                          ViewGroup.LayoutParams params = myshadow.getLayoutParams();
                    params.height = (int) (getResources().getDimensionPixelSize(R.dimen.fourdp)*Math.abs(progress));
                    myshadow.setLayoutParams(params);
                    }


                    mArrowImageView.setRotation(progress * 180);
                    mIsExpanded = verticalOffset == 0;
                    mAppBarIdle = mAppBarOffset >= 0 || mAppBarOffset <= mAppBarMaxOffset;
                    float alpha = (float) -verticalOffset / totalScrollRange;


                    if (mAppBarOffset == -appBarLayout.getTotalScrollRange()) {
                        isappbarclosed = true;
                        setExpandAndCollapseEnabled(false);
                    } else {
                        setExpandAndCollapseEnabled(true);
                    }

                    if (mAppBarOffset == 0) {
                        expandedfirst = linearLayoutManager.findFirstVisibleItemPosition();
                        if (mNestedView.getVisibility() == View.VISIBLE) {
                            topspace = linearLayoutManager.findViewByPosition(linearLayoutManager.findFirstVisibleItemPosition()).getTop();//uncomment jigs 28 feb
                        }
                        if (isappbarclosed) {
                            isappbarclosed = false;
                            mNestedView.stopScroll();

                            //linearLayoutManager.scrollToPositionWithOffset(expandedfirst,0);
                            calendarView.setCurrentmonth(lastdate);
                        }
                    }

                }


            }
        });

        findViewById(R.id.backsupport).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//
                        if (monthviewpager.getVisibility() == View.VISIBLE) return;
                        mIsExpanded = !mIsExpanded;
                        mNestedView.stopScroll();

                        mAppBar.setExpanded(mIsExpanded, true);


                    }
                });

        /////////////////weekview implemention/////
         myshadow=findViewById(R.id.myshadow);



        mWeekView.setshadow(myshadow);
        mWeekView.setfont( ResourcesCompat.getFont(this, R.font.googlesans_regular),0);
        mWeekView.setfont( ResourcesCompat.getFont(this, R.font.googlesansmed),1);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);
        mWeekView.setScrollListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocalDate mintime = new LocalDate().minusYears(5);
            LocalDate maxtime = new LocalDate().plusYears(5);
            alleventlist = Utility.readCalendarEvent(this, mintime, maxtime);
            calendarView.init(alleventlist,mintime, maxtime);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isgivepermission=true;
                    lastdate = new LocalDate();
                    calendarView.setCurrentmonth(new LocalDate());
                    calendarView.adjustheight();
                    mIsExpanded = false;
                    mAppBar.setExpanded(false, false);
                    mWeekView.notifyDatasetChanged();
//                    LinearLayoutManager linearLayoutManager= (LinearLayoutManager) mNestedView.getLayoutManager();
//                    if (indextrack.containsKey(new LocalDate())){
//                        smoothScroller.setTargetPosition(indextrack.get(new LocalDate()));
//                        linearLayoutManager.scrollToPositionWithOffset(indextrack.get(new LocalDate()),0);
//                    }
//                    else {
//                        for (int i=0;i<eventalllist.size();i++){
//                            if (eventalllist.get(i).getLocalDate().getMonthOfYear()==new LocalDate().getMonthOfYear()&&eventalllist.get(i).getLocalDate().getYear()==new LocalDate().getYear()){
//                                linearLayoutManager.scrollToPositionWithOffset(i,0);
//                                break;
//                            }
//                        }
//                    }
                }
            }, 10);
        }
    }

    /**
     * this call when user is scrolling on mNestedView(recyclerview) and month will change
     * or when toolbar top side current date button selected
     */
    @Subscribe
    public void onEvent(MonthChange event) {


        if (!isAppBarExpanded()) {

            LocalDate localDate = new LocalDate();
            String year = event.getMessage().getYear() == localDate.getYear() ? "" : event.getMessage().getYear() + "";
           // monthname.setText(event.getMessage().toString("MMMM") + " " + year);
            String transYear = event.getMessage().toString("MMMM") + " " + year;
            if (transYear.contains("January")) {
                monthNameEnglish = "January "+year;
                monthname.setText("MŨGAA "+year);
            }
            if (transYear.contains("February")) {
                monthNameEnglish = "February "+year;
                monthname.setText("MŨGETHO "+year);
            }
            if (transYear.contains("March")) {
                monthNameEnglish = "March "+year;
                monthname.setText("KĨHU "+year);
            }
            if (transYear.contains("April")) {
                monthNameEnglish = "April "+year;
                monthname.setText("MŨTHATŨ "+year);
            }
            if (transYear.contains("May")) {
                monthNameEnglish = "May "+year;
                monthname.setText("MŨGIRA NJARA "+year);
            }
            if (transYear.contains("June")) {
                monthNameEnglish = "June "+year;
                monthname.setText("GATHATHANWA "+year);
            }
            if (transYear.contains("July")) {
                monthNameEnglish = "July "+year;
                monthname.setText("GATHANO "+year);
            }
            if (transYear.contains("August")) {
                monthNameEnglish = "August "+year;
                monthname.setText("MUORIA NYONI "+year);
            }
            if (transYear.contains("September")) {
                monthNameEnglish = "September "+year;
                monthname.setText("MŨGAA WA KEERĨ "+year);
            }
            if (transYear.contains("October")) {
                monthNameEnglish = "October "+year;
                monthname.setText("MWANIA THENGE "+year);
            }
            if (transYear.contains("November")) {
                monthNameEnglish = "November "+year;
                monthname.setText("KANYUA HŨNGŨ  "+year);
            }
            if (transYear.contains("December")) {
                monthNameEnglish = "December "+year;
                monthname.setText("GATUMU "+year);
            }
            Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEER "+ year);
            Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEAR1111 "+ event.getMessage().toString("MMMM") + " " + year);

            long diff = System.currentTimeMillis() - lasttime;
            boolean check = diff > 600;
            if (check && event.mdy > 0) {
                monthname.setTranslationY(35);
                mArrowImageView.setTranslationY(35);
                lasttime = System.currentTimeMillis();
                monthname.animate().translationY(0).setDuration(200).start();
                mArrowImageView.animate().translationY(0).setDuration(200).start();

            } else if (check && event.mdy < 0) {

                monthname.setTranslationY(-35);
                mArrowImageView.setTranslationY(-35);
                lasttime = System.currentTimeMillis();
                monthname.animate().translationY(0).setDuration(200).start();
                mArrowImageView.animate().translationY(0).setDuration(200).start();
            }


        }

    }

    /**
     * call when Googlecalendarview is open and tap on any date or scroll viewpager available inside GoogleCalendar
     */
    @Subscribe
    public void onEvent(MessageEvent event) {

        int previous = lastchangeindex;
        if (previous != -1) {
            int totalremove = 0;
            for (int k = 1; k <= 3; k++) {

                if (eventalllist.get(previous).getEventname().equals("dupli") || eventalllist.get(previous).getEventname().equals("click")) {
                    totalremove++;
                    EventModel eventModel = eventalllist.remove(previous);
                }
            }
            indextrack.clear();
            indextrack.putAll(dupindextrack);
            mNestedView.getAdapter().notifyDataSetChanged();

        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
        if (indextrack.containsKey(event.getMessage())) {
            int index = indextrack.get(event.getMessage());
            int type = eventalllist.get(index).getType();
            if (type == 0 || type == 2) {

                lastdate = event.getMessage();
                expandedfirst = index;
                topspace = 20;
                linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);
                lastchangeindex = -1;

            } else {


                lastdate = event.getMessage();


                Integer ind = indextrack.get(event.getMessage());
                ind++;
                for (int i = ind; i < eventalllist.size(); i++) {


                    if (event.getMessage().isBefore(eventalllist.get(i).getLocalDate())) {
                        ind = i;
                        break;
                    }
                }
                lastchangeindex = ind;
                int typeselect = eventalllist.get(ind + 1).getType() == 200 ? 200 : 100;
                if (!eventalllist.get(ind - 1).getEventname().startsWith("dup")) {

                    eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
                    ind++;
                }
                expandedfirst = ind;
                eventalllist.add(ind, new EventModel("click", event.getMessage(), 1000));
                ind++;
                if (!eventalllist.get(ind).getEventname().startsWith("dup")) {

                    eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
                }
                mNestedView.getAdapter().notifyDataSetChanged();

                topspace = 20;
                linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);

                for (int i = lastchangeindex; i < eventalllist.size(); i++) {
                    if (!eventalllist.get(i).getEventname().startsWith("dup"))
                        indextrack.put(eventalllist.get(i).getLocalDate(), i);
                }


            }

        } else {
            Integer ind = indextrack.get(event.getMessage().dayOfWeek().withMinimumValue().minusDays(1));
            ind++;
            for (int i = ind; i < eventalllist.size(); i++) {

                if (event.getMessage().isBefore(eventalllist.get(i).getLocalDate())) {
                    ind = i;
                    break;
                }
            }
            lastchangeindex = ind;
            int typeselect = eventalllist.get(ind + 1).getType() == 200 ? 200 : 100;
            if (!eventalllist.get(ind - 1).getEventname().startsWith("dup")) {

                eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
                ind++;
            }
            expandedfirst = ind;

            eventalllist.add(ind, new EventModel("click", event.getMessage(), 1000));
            ind++;
            if (!eventalllist.get(ind).getEventname().startsWith("dup")) {

                eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
            }

            mNestedView.getAdapter().notifyDataSetChanged();
            topspace = 20;
            linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);

            for (int i = lastchangeindex; i < eventalllist.size(); i++) {
                if (!eventalllist.get(i).getEventname().startsWith("dup"))
                    indextrack.put(eventalllist.get(i).getLocalDate(), i);
            }

        }

    }

    private int getDeviceHeight() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int height1 = size.y;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return height1;
    }

    private int getDevicewidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width;
    }

    @Override
    public void onBackPressed() {
        if (closebtn.getVisibility() == View.VISIBLE) {
            closebtnClick();

        } else if (mIsExpanded) {
            mIsExpanded = false;
            mNestedView.stopScroll();
            mAppBar.setExpanded(false, true);
        } else if (mNestedView.getVisibility() == View.VISIBLE) {
            monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(MainActivity.lastdate), false);

            mNestedView.setVisibility(View.GONE);
            monthviewpager.setVisibility(View.VISIBLE);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(false);
            mAppBar.setElevation(0);
            mArrowImageView.setVisibility(View.INVISIBLE);
        } else {
            EventBus.getDefault().unregister(this);
            super.onBackPressed();
            finish();
        }


    }

    /**
     * call only one time after googlecalendarview init() method is done
     */
    @Subscribe
    public void onEvent(final AddEvent event) {
        eventalllist = event.getArrayList();



        final TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {

            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            int monthheight = getDeviceHeight() - actionBarHeight - getnavigationHeight() - getStatusBarHeight();
            int recyheight = monthheight - getResources().getDimensionPixelSize(R.dimen.monthtopspace);
            int singleitem = (recyheight - 18) / 6;

                //monthviewpager.setAdapter(new MonthViewPagerAdapter(MainActivity.this,event.getMonthModels(),singleitem));
                monthviewpager.setAdapter(new MonthPageAdapter(getSupportFragmentManager(), event.getMonthModels(), singleitem));
                monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(LocalDate.now()), false);

//            monthviewpager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    Log.e("ffheight",monthviewpager.getMeasuredHeight()+"");
//
//                }
//            });

//            monthviewpager.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
//                @Override
//                public void onDraw() {
//                    monthviewpager.getViewTreeObserver().removeOnDrawListener(this);
//                    Log.e("ffheight",monthviewpager.getMeasuredHeight()+"");
//                    int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
//                    int monthheight  = getDeviceHeight()-actionBarHeight-getnavigationHeight();
//                    Log.e("monthheight",monthheight+"");
//                    int recyheight = monthheight-getResources().getDimensionPixelSize(R.dimen.monthtopspace)-getResources().getDimensionPixelSize(R.dimen.pluseightdp);
//                    int singleitem = (recyheight-20)/6;
//                    if (monthviewpager.getVisibility()== View.VISIBLE){
//                        //monthviewpager.setAdapter(new MonthViewPagerAdapter(MainActivity.this,event.getMonthModels(),singleitem));
//                        monthviewpager.setAdapter(new MonthPageAdapter(getSupportFragmentManager(),event.getMonthModels(),singleitem));
//                        monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(LocalDate.now()),false);
//                    }
//                }
//            });


        }


        indextrack = event.getIndextracker();
        for (Map.Entry<LocalDate, Integer> entry : indextrack.entrySet()) {
            dupindextrack.put(entry.getKey(), entry.getValue());
        }

        if (mNestedView.isAttachedToWindow()) {

            mNestedView.getAdapter().notifyDataSetChanged();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LocalDate localDate = new LocalDate();
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
                if (indextrack.containsKey(LocalDate.now())) {

                    Integer val = indextrack.get(LocalDate.now());
                    expandedfirst = val;
                    topspace = 20;
                    linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);
                    EventBus.getDefault().post(new MonthChange(localDate, 0));
                    month = localDate.getDayOfMonth();
                    lastdate = localDate;


                }
            }
        }, 100);


    }

    private void setExpandAndCollapseEnabled(boolean enabled) {

//        RecyclerView recyclerView= (RecyclerView) monthviewpager.getChildAt(0);
//        GooglecalenderView.MonthPagerAdapter.MonthViewHolder monthViewHolder= (GooglecalenderView.MonthPagerAdapter.MonthViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
//        if (monthViewHolder!=null&&monthViewHolder.gridview!=null&&monthViewHolder.gridview.getAdapter()!=null){
//            monthViewHolder.gridview.getAdapter().notifyDataSetChanged();
//        }
//        if (mNestedView.isNestedScrollingEnabled() != enabled) {
//            ViewCompat.setNestedScrollingEnabled(recyclerView,enabled);
//        }
        if (mNestedView.isNestedScrollingEnabled() != enabled) {
            ViewCompat.setNestedScrollingEnabled(mNestedView, enabled);
        }

    }

    @Override
    public boolean isAppBarClosed() {
        return isappbarclosed;
    }

    @Override
    public int appbaroffset() {
        return expandedfirst;
    }

    public void selectdateFromMonthPager(int year, int month, int day) {
        MainActivity.lastdate = new LocalDate(year, month, day);
        LocalDate localDate = new LocalDate();
        String yearstr = MainActivity.lastdate.getYear() == localDate.getYear() ? "" : MainActivity.lastdate.getYear() + "";
       // monthname.setText(MainActivity.lastdate.toString("MMMM") + " " + yearstr);
        String transYear = MainActivity.lastdate.toString("MMMM") + " " + yearstr;
        if (transYear.contains("January")) {
            monthNameEnglish = "January "+yearstr;
            monthname.setText("MŨGAA "+yearstr);
        }
        if (transYear.contains("February")) {
            monthNameEnglish = "February "+yearstr;
            monthname.setText("MŨGETHO "+yearstr);
        }
        if (transYear.contains("March")) {
            monthNameEnglish = "March "+yearstr;
            monthname.setText("KĨHU "+yearstr);
        }
        if (transYear.contains("April")) {
            monthNameEnglish = "April "+yearstr;
            monthname.setText("MŨTHATŨ "+yearstr);
        }
        if (transYear.contains("May")) {
            monthNameEnglish = "May "+yearstr;
            monthname.setText("MŨGIRA NJARA "+yearstr);
        }
        if (transYear.contains("June")) {
            monthNameEnglish = "June "+yearstr;
            monthname.setText("GATHATHANWA "+yearstr);
        }
        if (transYear.contains("July")) {
            monthNameEnglish = "July "+yearstr;
            monthname.setText("GATHANO "+yearstr);
        }
        if (transYear.contains("August")) {
            monthNameEnglish = "August "+yearstr;
            monthname.setText("MUORIA NYONI "+yearstr);
        }
        if (transYear.contains("September")) {
            monthNameEnglish = "September "+yearstr;
            monthname.setText("MŨGAA WA KEERĨ "+yearstr);
        }
        if (transYear.contains("October")) {
            monthNameEnglish = "October "+yearstr;
            monthname.setText("MWANIA THENGE "+yearstr);
        }
        if (transYear.contains("November")) {
            monthNameEnglish = "November "+yearstr;
            monthname.setText("KANYUA HŨNGŨ "+year);
        }
        if (transYear.contains("December")) {
            monthNameEnglish = "December "+yearstr;
            monthname.setText("GATUMU "+yearstr);
        }
        Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEER "+ yearstr);
        Log.d(TAG, "onNavigationItemSelected: YEEEEEEEEAR54543 "+ MainActivity.lastdate.toString("MMMM") + " " + yearstr);
        calendarView.setCurrentmonth(MainActivity.lastdate);
        calendarView.adjustheight();
        mIsExpanded = false;
        mAppBar.setExpanded(false, false);
        EventBus.getDefault().post(new MessageEvent(new LocalDate(year, month, day)));
        monthviewpager.setVisibility(View.GONE);
        mNestedView.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
        ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
        mAppBar.setElevation(20);
        mArrowImageView.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean isAppBarExpanded() {

        return mAppBarOffset == 0;
    }

    @Override
    public boolean isAppBarIdle() {
        return mAppBarIdle;
    }




    class MonthPageAdapter extends FragmentStatePagerAdapter {
        private ArrayList<MonthModel> monthModels;
        private int singleitemheight;

        // private ArrayList<MonthFragment> firstFragments=new ArrayList<>();

        public MonthPageAdapter(FragmentManager fragmentManager, ArrayList<MonthModel> monthModels, int singleitemheight) {

            super(fragmentManager);
            this.monthModels = monthModels;
            this.singleitemheight = singleitemheight;

//            for (int position=0;position<monthModels.size();position++){
//                firstFragments.add(MonthFragment.newInstance(monthModels.get(position).getMonth(), monthModels.get(position).getYear(), monthModels.get(position).getFirstday(), monthModels.get(position).getDayModelArrayList(), alleventlist, singleitemheight));
//            }
        }

//        public ArrayList<MonthFragment> getFirstFragments() {
//            return firstFragments;
//        }

        public ArrayList<MonthModel> getMonthModels() {
            return monthModels;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return monthModels.size();
        }


        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return MonthFragment.newInstance(monthModels.get(position).getMonth(), monthModels.get(position).getYear(), monthModels.get(position).getFirstday(), monthModels.get(position).getDayModelArrayList(), alleventlist, singleitemheight);
        }


        // Returns the page title for the top indicator


    }

//    class MonthViewPagerAdapter extends RecyclerView.Adapter<MonthViewPagerAdapter.MonthHolder> {
//        private ArrayList<MonthModel> monthModels;
//        private int singleitemheight;
//        private Context context;
//
//        public MonthViewPagerAdapter(Context context, ArrayList<MonthModel> monthModels, int singleitemheight) {
//
//            this.monthModels = monthModels;
//            this.singleitemheight = singleitemheight;
//            this.context = context;
//        }
//
//        @NonNull
//        @Override
//        public MonthHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(context).inflate(R.layout.fragment_month, parent, false);
//            return new MonthHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull MonthHolder holder, int position) {
//
//            Myadapter myadapter = new Myadapter(context, monthModels.get(position).getMonth(), monthModels.get(position).getYear(), monthModels.get(position).getFirstday(), monthModels.get(position).getDayModelArrayList(), alleventlist, singleitemheight);
//            holder.gridView.setAdapter(myadapter);
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return monthModels.size();
//
//        }
//
//        class MonthHolder extends RecyclerView.ViewHolder {
//            RecyclerView gridView;
//
//            public MonthHolder(@NonNull View itemView) {
//                super(itemView);
//                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
//                gridView = itemView.findViewById(R.id.recyclerview);
//                gridView.setLayoutManager(gridLayoutManager);
//                MiddleDividerItemDecoration vertecoration = new MiddleDividerItemDecoration(context, DividerItemDecoration.VERTICAL);
//                //vertecoration.setDrawable(new ColorDrawable(Color.LTGRAY));
//                MiddleDividerItemDecoration hortdecoration = new MiddleDividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
//                // hortdecoration.setDrawable(new ColorDrawable(Color.LTGRAY));
//                gridView.addItemDecoration(vertecoration);
//                gridView.addItemDecoration(hortdecoration);
//
//            }
//        }
//    }

//    class Myadapter extends RecyclerView.Adapter<Myadapter.MonthViewHolder> {
//
//        private Context context;
//        private int singleitemheight;
//        private int month;
//        private int year;
//        private int firstday;
//        private ArrayList<DayModel> dayModels;
//
//        public Myadapter(Context context, int month, int year, int page, ArrayList<DayModel> dayModels, HashMap<LocalDate, EventInfo> alleventlist, int singleitemheight) {
//            this.context = context;
//            this.singleitemheight = singleitemheight;
//            this.month = month;
//            this.year = year;
//            this.firstday = page;
//            LocalDate prevmonth = new LocalDate(year, month, 1);
//
//            ArrayList<DayModel> adapterdata = new ArrayList<>(43);
//            for (int i = 0; i < 42; i++) {
//                if (i < page) {
//                    LocalDate localDate = prevmonth.minusDays(page - i);
//                    DayModel dayModel = new DayModel();
//                    dayModel.setDay(localDate.getDayOfMonth());
//                    dayModel.setMonth(localDate.getMonthOfYear());
//                    dayModel.setYear(localDate.getYear());
//                    if (alleventlist.containsKey(localDate)) {
//                        dayModel.setEvents(alleventlist.get(localDate).eventtitles);
//                    }
//
//                    dayModel.setIsenable(false);
//                    adapterdata.add(dayModel);
//
//                } else if (i >= dayModels.size() + page) {
//
//                    LocalDate localDate = prevmonth.plusDays(i - (page));
//                    DayModel dayModel = new DayModel();
//                    dayModel.setDay(localDate.getDayOfMonth());
//                    dayModel.setMonth(localDate.getMonthOfYear());
//                    dayModel.setYear(localDate.getYear());
//                    dayModel.setIsenable(false);
//                    if (alleventlist.containsKey(localDate)) {
//                        dayModel.setEvents(alleventlist.get(localDate).eventtitles);
//                    }
//                    adapterdata.add(dayModel);
//                } else {
//                    DayModel dayModel = dayModels.get(i - page);
//                    dayModel.setIsenable(true);
//                    LocalDate mydate = new LocalDate(year, month, dayModel.getDay());
//                    if (alleventlist.containsKey(mydate)) {
//                        dayModel.setEvents(alleventlist.get(mydate).eventtitles);
//                    }
//                    adapterdata.add(dayModels.get(i - page));
//
//                }
//            }
//            this.dayModels = adapterdata;
//        }
//
//        @Override
//        public Myadapter.MonthViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
//
//            View view = LayoutInflater.from(context).inflate(R.layout.monthgriditem, parent, false);
//            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//            layoutParams.height = singleitemheight;
//            view.setLayoutParams(layoutParams);
//            return new Myadapter.MonthViewHolder(view);
//
//        }
//
//
//        @Override
//        public void onBindViewHolder(Myadapter.MonthViewHolder holder, int position) {
//
//
//            holder.textView.setText(dayModels.get(position).getDay() + "");
//            if (dayModels.get(position).isenable()) {
//                holder.textView.setTextColor(Color.BLACK);
//            } else {
//                holder.textView.setTextColor(getResources().getColor(R.color.lightblack));
//
//            }
//
//            String names[] = dayModels.get(position).getEvents();
//            if (names != null) {
//                if (names.length == 1) {
//                    holder.event1.setVisibility(View.VISIBLE);
//                    holder.event2.setVisibility(View.GONE);
//                    holder.event3.setVisibility(View.GONE);
//                    holder.event2.setText("");
//                    holder.event3.setText("");
//                } else if (names.length == 2) {
//                    holder.event1.setVisibility(View.VISIBLE);
//                    holder.event2.setVisibility(View.VISIBLE);
//                    holder.event3.setVisibility(View.GONE);
//                    holder.event3.setText("");
//
//                } else {
//                    holder.event1.setVisibility(View.VISIBLE);
//                    holder.event2.setVisibility(View.VISIBLE);
//                    holder.event3.setVisibility(View.VISIBLE);
//                }
//                for (int i = 0; i < dayModels.get(position).getEvents().length; i++) {
//                    if (i == 0) holder.event1.setText(names[0]);
//                    else if (i == 1) holder.event2.setText(names[1]);
//                    else holder.event3.setText(names[2]);
//
//                }
//            } else {
//                holder.event1.setVisibility(View.GONE);
//                holder.event2.setVisibility(View.GONE);
//                holder.event3.setVisibility(View.GONE);
//
//            }
//
//
//        }
//
//        @Override
//        public int getItemCount() {
//
//            return 42;
//        }
//
//        class MonthViewHolder extends RecyclerView.ViewHolder {
//
//            private TextView textView;
//            private TextView event1;
//            private TextView event2;
//            private TextView event3;
//
//            public MonthViewHolder(View itemView) {
//                super(itemView);
//                textView = itemView.findViewById(R.id.textView8);
//                event1 = itemView.findViewById(R.id.event1);
//                event2 = itemView.findViewById(R.id.event2);
//                event3 = itemView.findViewById(R.id.event3);
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
//
//            }
//        }
//    }

    public class DateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

        LocalDate today = LocalDate.now();

        public ArrayList<EventModel> geteventallList() {
            return eventalllist;
        }

        @Override
        public int getItemViewType(int position) {
            if (position > 1 && eventalllist.get(position).getType() == 0 && getHeaderId(position) == getHeaderId(position - 1))
                return 5;
            if (position > 1 && eventalllist.get(position).getType() == 3 && eventalllist.get(position - 1).getType() == 1)
                return 7;
            if (position + 1 < eventalllist.size() && eventalllist.get(position).getType() == 3 && (eventalllist.get(position + 1).getType() == 1 || eventalllist.get(position + 1).getType() == 0))
                return 6;
            return eventalllist.get(position).getType();
        }

        public int getHeaderItemViewType(int position) {
            return eventalllist.get(position).getType();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateHeaderViewHolder: VVVVVVIEEEE TYPPPPPE000 ");
            if (viewType == 0) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_item, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == 5) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewitemlessspace, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == 100) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.extraspace, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewType == 200) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.liitlespace, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewType == 1) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewlast, parent, false);
                return new EndViewHolder(view);
            } else if (viewType == 2) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noplanlay, parent, false);
                return new NoplanViewHolder(view);
            } else if (viewType == 1000) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noplanlittlespace, parent, false);
                return new NoplanViewHolder(view);
            } else if (viewType == 6) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelayextrabottomspace, parent, false);
                return new RangeViewHolder(view);
            } else if (viewType == 7) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelayextratopspace, parent, false);
                return new RangeViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelay, parent, false);
                return new RangeViewHolder(view);
            }

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            int viewtype = getItemViewType(position);

            if (viewtype == 0 || viewtype == 5) {

                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                GradientDrawable shape =  new GradientDrawable();
                shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
                shape.setColor(eventalllist.get(position).getColor());
              //  GradientDrawable drawable = (GradientDrawable) holder.eventtextview.getBackground();

//               if (eventalllist.get(position).getType()==0)drawable.setColor(eventalllist.get(position).getColor());
//               else drawable.setColor(Color.BLACK);
                holder.eventtextview.setBackground(shape);
                holder.eventtextview.setText(eventalllist.get(position).getEventname());


                if (position + 1 < eventalllist.size() && eventalllist.get(position).getLocalDate().equals(today) && (!eventalllist.get(position + 1).getLocalDate().equals(today) || eventalllist.get(position + 1).getType() == 100 || eventalllist.get(position + 1).getType() == 200)) {
                    holder.circle.setVisibility(View.VISIBLE);
                    holder.line.setVisibility(View.VISIBLE);

                } else {
                    holder.circle.setVisibility(View.GONE);
                    holder.line.setVisibility(View.GONE);
                }
            } else if (viewtype == 1) {

                EndViewHolder holder = (EndViewHolder) viewHolder;
                holder.eventimageview.setImageResource(monthresource[eventalllist.get(position).getLocalDate().getMonthOfYear() - 1]);

                Log.d(TAG, "onBindViewHolder: VUZIGUUUU " +eventalllist.get(position).getLocalDate().toString("MMMM YYYY"));
                String monthName = eventalllist.get(position).getLocalDate().toString("MMMM");
                String monthYear = eventalllist.get(position).getLocalDate().toString("YYYY");
                if (monthName.equals("January")){
                    holder.monthname.setText("MŨGAA "+monthYear);
                    holder.monthdesc.setText("-Nĩ hĩndĩ ya riũa ihiũ mũno, mũtĩ wa mũgaa noguo wetiragia.\n\n" +
                            "-Mũgaa is the Acacia Tree. A time when the sun is very hot and only the Acacia tree thrives.");
                }
                if (monthName.equals("February")){
                    holder.monthname.setText("MŨGETHO "+monthYear);
                    holder.monthdesc.setText("-Mũgetho waarĩ mweri wa magetha\n\n" +
                            "-Mũgetho - This is the harvest period");
                }
                if (monthName.equals("March")){
                    holder.monthname.setText("KĨHU "+monthYear);
                    holder.monthdesc.setText("-Matu makoragwo mathimbĩte na magacuha ta me na ihu rĩa mbura yetereirwo ĩitĩke mũthatũ\n\n" +
                            "-This is the month when the clouds are pregnant with rain awaiting to fall next soon");
                }
                if (monthName.equals("April")){
                    holder.monthname.setText("MŨTHATŨ "+monthYear);
                    holder.monthdesc.setText("-Igũnyũ cia thaatũ cikoragwo irĩ nyingĩ nĩ ũndũ mahuti nĩ maingĩ na kĩbii onakĩo gĩkoragwo gĩtumanĩte nĩ mbura.\n\n" +
                            "-Green worms are plenty at this time because the leaves are plentiful and the rains are falling");
                }
                if (monthName.equals("May")){
                    holder.monthname.setText("MŨGIRA NJARA "+monthYear);
                    holder.monthdesc.setText("-Mũgĩkũyũ nĩ atithagia, akarũgamia magongona mweri-inĩ ũyũ\n\n" +
                            "-During this period the Mũgĩkũyũ stops performing ceremonies ");
                }
                if (monthName.equals("June")){
                    holder.monthname.setText("GATHATHANWA "+monthYear);
                    holder.monthdesc.setText("-GATHATHANWA heho nĩ yambĩrĩria, andũ no kũrigitha moko maigue rugarĩ\n\n" +
                            "-In this period the biting cold has began and people are rubbing their hands for warmth.");
                }
                if (monthName.equals("July")){
                    holder.monthname.setText("GATHANO "+monthYear);
                    holder.monthdesc.setText("-GATHANO, nĩ hĩndĩ heho ĩcacĩte, andũ othe manyitĩte thano nĩ heho\n\n" +
                            "-This is the period of extreme cold and everyone is clenching their fists to keep warm ( thano – fist ).");
                }
                if (monthName.equals("August")){
                    holder.monthname.setText("MUORIA NYONI "+monthYear);
                    holder.monthdesc.setText("-MUORIA NYONI, Nyoni irarĩa kinya ikoria nda nĩ kũhũũna. Kuora nĩ kũbutha\n\n" +
                            "-Birds have eaten beyond their stomachs can hold. Their stomachs are now rotting.");
                }
                if (monthName.equals("September")){
                    holder.monthname.setText("MŨGAA WA KEERĨ "+monthYear);
                    holder.monthdesc.setText("-Mũgaa wa kerĩ, riũa rĩhiũhĩte o ta Mũgaa. Magetha ma keerĩ nĩrĩo magethagwo nĩ ũndũ matingĩbutha nĩ momĩtio nĩ riũa\n\n" +
                            "-This is the second harvest. The sun is as hot as in Mũgaa, the cereals have been dried up by the sun's heat they cannot rot.");
                }
                if (monthName.equals("October")){
                    holder.monthname.setText("MWANIA THENGE "+monthYear);
                    holder.monthdesc.setText("-MWANIA THENGE nĩrĩo mũgĩkũyũ aracagia mbũri ciyũire njĩra no kwania\n\n" +
                            "-Mwania thenge is the season for paying dowry. Goats and sheep are \"shouting\" in the roads as they are taken for dowry.");
                }
                if (monthName.equals("November")){
                    holder.monthname.setText("KANYUA HŨNGŨ "+monthYear);
                    holder.monthdesc.setText("-KANYUA HŨNGŨ nĩrĩo mũgĩkũyũ athĩnjaga mbũri iria araracĩirio Nyoni ciothe ndĩa nyama irĩ rĩerainĩ nĩ ũndũ nyama nĩ nyingĩ\n\n" +
                            "-KANYUA HŨNGŨ is when the goats and sheep paid as dowry price are being slaughtered, all meat eating birds are flying in the air in search of meat as it is in plenty.");
                }
                if (monthName.equals("December")){
                    holder.monthname.setText("GATUMU "+monthYear);
                    holder.monthdesc.setText("-GATUMU nĩrĩo ciũndũ iratumũrwo iria iraracirio, ũguo Gatumu gagatumũrwo\n\n" +
                            "-Gatumu is when the baskets given during dowry are being opened, now what was sealed is being opened.");
                }


            } else if (viewtype == 2 || viewtype == 100 || viewtype == 200 || viewtype == 1000) {

            } else {
                RangeViewHolder holder = (RangeViewHolder) viewHolder;
                holder.rangetextview.setText(eventalllist.get(position).getEventname().replaceAll("tojigs", ""));
            }

        }

        @Override
        public long getHeaderId(int position) {
            if (eventalllist.get(position).getType() == 1) return position;
            else if (eventalllist.get(position).getType() == 3) return position;
            else if (eventalllist.get(position).getType() == 100) return position;
            else if (eventalllist.get(position).getType() == 200) return position;
            LocalDate localDate = eventalllist.get(position).getLocalDate();
            String uniquestr = "" + localDate.getDayOfMonth() + localDate.getMonthOfYear() + localDate.getYear();
            return Long.parseLong(uniquestr);

        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int position) {
            int viewtype = getHeaderItemViewType(position);
            Log.d(TAG, "onCreateHeaderViewHolder: VVVVVVIEEEE TYPPPPPE111 ");
            if (viewtype == 2) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todayheader, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewtype == 0 && eventalllist.get(position).getLocalDate().equals(today)) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todayheader, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewtype == 1 || viewtype == 3 || viewtype == 100 || viewtype == 200) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.headerview, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }

        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewtype = getHeaderItemViewType(position);
            if (viewtype == 0 || viewtype == 2 || viewtype == 1000) {
                TextView vartextView = holder.itemView.findViewById(R.id.textView9);
                TextView vartextView2 = holder.itemView.findViewById(R.id.textView99);
                TextView datetextView = holder.itemView.findViewById(R.id.textView10);
                Log.d(TAG, "onBindHeaderViewHolder: MMMMMMY DAAAY "+var[eventalllist.get(position).getLocalDate().getDayOfWeek() - 1]);
                vartextView.setText(var[eventalllist.get(position).getLocalDate().getDayOfWeek() - 1]);
                datetextView.setText(eventalllist.get(position).getLocalDate().getDayOfMonth() + "");
                String daaa = var[eventalllist.get(position).getLocalDate().getDayOfWeek() - 1];
//                {"Kerĩ", "Gatatũ", "Kana", "Gatano", "Gatandatũ", "Mũtua Rũru", "Kiumia"}
                if (daaa.equals("Kerĩ")) {
                    vartextView2.setText("Monday");
                }
                if (daaa.equals("Gatatũ")) {
                    vartextView2.setText("Tuesday");
                }
                if (daaa.equals("Kana")) {
                    vartextView2.setText("Wednesday");
                }
                if (daaa.equals("Gatano")) {
                    vartextView2.setText("Thursday");
                }
                if (daaa.equals("Gatandatũ")) {
                    vartextView2.setText("Friday");
                }
                if (daaa.equals("Mũtua Rũru")) {
                    vartextView2.setText("Saturday");
                }
                if (daaa.equals("Kiumia")) {
                    vartextView2.setText("Sunday");
                }


                holder.itemView.setTag(position);
            } else {
                Log.d(TAG, "onBindHeaderViewHolder: NNNNNOOOOOOOO VIEWWWWW "+ viewtype);

            }

        }

        @Override
        public int getItemCount() {
            return eventalllist.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView eventtextview;
            View circle, line;

            public ItemViewHolder(View itemView) {
                super(itemView);
                eventtextview = itemView.findViewById(R.id.view_item_textview);
                eventtextview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (isAppBarExpanded()) {
                            mIsExpanded = !mIsExpanded;
                            mNestedView.stopScroll();

                            mAppBar.setExpanded(mIsExpanded, true);
                            return;
                        }
                       EventInfo eventInfo= alleventlist.get(eventalllist.get(getAdapterPosition()).getLocalDate());
                        String sfs=eventalllist.get(getAdapterPosition()).getEventname();
                        while (eventInfo!=null&&!sfs.startsWith(eventInfo.title)){
                            eventInfo=eventInfo.nextnode;
                        }

                        eventnametextview.setText(eventInfo.title);

                        if (eventInfo.isallday==false){
                            LocalDateTime start=new LocalDateTime(eventInfo.starttime, DateTimeZone.forID(eventInfo.timezone));
                            LocalDateTime end=new LocalDateTime(eventInfo.endtime, DateTimeZone.forID(eventInfo.timezone));
                            String sf=start.toString("a").equals(end.toString("a"))?"":"a";
                            String rangetext = daysList[start.getDayOfWeek()] + ", " + start.toString("d MMM") + " · " + start.toString("h:mm "+sf+"") + " - " + end.toString("h:mm a");
                            eventrangetextview.setText(rangetext);
                        }
                        else if (eventInfo.noofdayevent>1){
                            LocalDate localDate=new LocalDate(eventInfo.starttime, DateTimeZone.forID(eventInfo.timezone));
                            LocalDate todaydate = LocalDate.now();
                            LocalDate nextday = localDate.plusDays(eventInfo.noofdayevent-1);
                            if (localDate.getYear() == todaydate.getYear()) {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM");
                                eventrangetextview.setText(rangetext);
                            } else {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM, YYYY");
                                eventrangetextview.setText(rangetext);
                            }
                        }
                        else {
                            LocalDate localDate = new LocalDate(eventInfo.starttime);
                            LocalDate todaydate = LocalDate.now();
                            if (localDate.getYear() == todaydate.getYear()) {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") ;
                                eventrangetextview.setText(rangetext);
                            } else {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") ;
                                eventrangetextview.setText(rangetext);
                            }
                        }

                        holidaytextview.setText(eventInfo.accountname);
                        closebtn.setVisibility(View.VISIBLE);
                        eventnametextview.setVisibility(View.GONE);
                        roundrect.setVisibility(View.GONE);
                        eventrangetextview.setVisibility(View.GONE);
                        calendaricon.setVisibility(View.GONE);
                        holidaytextview.setVisibility(View.GONE);
                        eventfixstextview.setVisibility(View.GONE);

                        final View view = mNestedView.getLayoutManager().findViewByPosition(getAdapterPosition());
                        ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                        layoutParams.height = v.getHeight();
                        layoutParams.width = v.getWidth();
                        redlay.setLayoutParams(layoutParams);
                        redlay.setTranslationX(v.getLeft());
                        redlay.setTranslationY(view.getTop() + toolbar.getHeight());
                        redlay.setTranslationZ(0);

                            GradientDrawable shape =  new GradientDrawable();
                            shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
                            mycolor=eventalllist.get(getAdapterPosition()).getColor();
                            shape.setColor(mycolor);
                            redlay.setBackground(shape);
                            roundrect.setBackground(shape);



                        ValueAnimator animwidth = ValueAnimator.ofInt(redlay.getWidth(), getDevicewidth());
                        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                                layoutParams.width = val;
                                redlay.setLayoutParams(layoutParams);
                            }
                        });
                        animwidth.setDuration(300);

                        ValueAnimator animheight = ValueAnimator.ofInt(redlay.getHeight(), getDeviceHeight());
                        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                                layoutParams.height = val;
                                redlay.setLayoutParams(layoutParams);
                                if (redlay.getTranslationZ() == 0 && valueAnimator.getAnimatedFraction() > 0.15) {
                                    redlay.setBackgroundColor(Color.WHITE);
                                    shadow.setVisibility(View.VISIBLE);
                                    redlay.setTranslationZ(getResources().getDimensionPixelSize(R.dimen.tendp));
                                }
                            }
                        });
                        animheight.setDuration(300);

                        ValueAnimator animx = ValueAnimator.ofFloat(redlay.getTranslationX(), 0);
                        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                Float val = (Float) valueAnimator.getAnimatedValue();
                                redlay.setTranslationX(val);
                            }
                        });
                        animx.setDuration(300);

                        ValueAnimator animy = ValueAnimator.ofFloat(redlay.getTranslationY(), 0);
                        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                Float val = (Float) valueAnimator.getAnimatedValue();
                                redlay.setTranslationY(val);
                            }
                        });
                        animy.setDuration(300);

                        animheight.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        closebtn.setVisibility(View.VISIBLE);
                                        eventnametextview.setVisibility(View.VISIBLE);
                                        roundrect.setVisibility(View.VISIBLE);
                                        eventrangetextview.setVisibility(View.VISIBLE);
                                        calendaricon.setVisibility(View.VISIBLE);
                                        holidaytextview.setVisibility(View.VISIBLE);
                                        eventfixstextview.setVisibility(View.VISIBLE);
                                    }
                                }, 150);

                            }
                        });
                        animwidth.start();
                        animheight.start();
                        animy.start();
                        animx.start();
                        eventview = v;
                        fullview = view;

                    }
                });
                circle = itemView.findViewById(R.id.circle);
                line = itemView.findViewById(R.id.line);
            }
        }

        class EndViewHolder extends RecyclerView.ViewHolder {
            ScrollParallaxImageView eventimageview;
            TextView monthname, monthdesc;

            public EndViewHolder(View itemView) {
                super(itemView);
                eventimageview = itemView.findViewById(R.id.imageView);
                eventimageview.setParallaxStyles(new VerticalMovingStyle());
                monthname = itemView.findViewById(R.id.textView11);
                monthdesc = itemView.findViewById(R.id.monthdesc);
            }
        }

        class NoplanViewHolder extends RecyclerView.ViewHolder {
            TextView noplantextview;

            public NoplanViewHolder(View itemView) {
                super(itemView);
                noplantextview = itemView.findViewById(R.id.view_noplan_textview);
            }
        }

        class RangeViewHolder extends RecyclerView.ViewHolder {

            TextView rangetextview;

            public RangeViewHolder(View itemView) {
                super(itemView);
                rangetextview = itemView.findViewById(R.id.view_range_textview);
            }
        }
    }



    ///////////////////////////////////weekview implemention///////////////////////////////////////

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        if (!isgivepermission)return new ArrayList<>();
        LocalDate initial = new LocalDate(newYear,newMonth,1);
        int length=initial.dayOfMonth().getMaximumValue();
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        for (int i=1;i<=length;i++){
            LocalDate localDate=new LocalDate(newYear,newMonth,i);
            if (alleventlist.containsKey(localDate)){
                EventInfo eventInfo=alleventlist.get(localDate);
                while (eventInfo!=null){
                    Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone(eventInfo.timezone));
                    startTime.setTimeInMillis(eventInfo.starttime);
                    Calendar endTime = (Calendar) Calendar.getInstance(TimeZone.getTimeZone(eventInfo.timezone));
                    endTime.setTimeInMillis(eventInfo.endtime);
                   int dau= Days.daysBetween(new LocalDate(eventInfo.endtime), new LocalDate(eventInfo.starttime)).getDays();

                    WeekViewEvent event = new WeekViewEvent(eventInfo.id, eventInfo.title, startTime, endTime,eventInfo.accountname);

                    event.setAllDay(eventInfo.isallday);
                    event.setColor(eventInfo.eventcolor);
//                    if (eventInfo.isallday)event.setColor(getResources().getColor(R.color.event_color_04));
//                    else event.setColor(getResources().getColor(R.color.event_color_02));
                    events.add(event);
                    eventInfo=eventInfo.nextnode;
                }
            }
        }



        return events;
    }


    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretday(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (mWeekView.getNumberOfVisibleDays()==7)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() ;
            }

            @Override
            public String interpretDate(Calendar date) {
                int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);


                return dayOfMonth+"" ;
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        if (isAppBarExpanded()) {
            mIsExpanded = !mIsExpanded;
            mNestedView.stopScroll();

            mAppBar.setExpanded(mIsExpanded, true);
            return;
        }
        eventnametextview.setText(event.getName());
        if (event.isAllDay()==false){
            LocalDateTime start=new LocalDateTime(event.getStartTime().getTimeInMillis(), DateTimeZone.forTimeZone(event.getStartTime().getTimeZone()));
            LocalDateTime end=new LocalDateTime(event.getEndTime().getTimeInMillis(), DateTimeZone.forTimeZone(event.getEndTime().getTimeZone()));
            String sf=start.toString("a").equals(end.toString("a"))?"":"a";
            String rangetext = daysList[start.getDayOfWeek()] + ", " + start.toString("d MMM") + " · " + start.toString("h:mm "+sf+"") + " - " + end.toString("h:mm a");
            eventrangetextview.setText(rangetext);
        }
        else if (event.isIsmoreday()){
            LocalDate localDate = new LocalDate(event.getActualstart().getTimeInMillis(), DateTimeZone.forTimeZone(event.getStartTime().getTimeZone()));
            LocalDate todaydate = LocalDate.now();
            LocalDate nextday = localDate.plusDays((int) (event.getNoofday()-1));
            if (localDate.getYear() == todaydate.getYear()) {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM");
                eventrangetextview.setText(rangetext);
            } else {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM, YYYY");
                eventrangetextview.setText(rangetext);
            }
        }
        else {
            LocalDate localDate = new LocalDate(event.getStartTime().getTimeInMillis());
            LocalDate todaydate = LocalDate.now();
            if (localDate.getYear() == todaydate.getYear()) {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") ;
                eventrangetextview.setText(rangetext);
            } else {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") ;
                eventrangetextview.setText(rangetext);
            }
        }

        holidaytextview.setText(event.getAccountname());
        closebtn.setVisibility(View.VISIBLE);
        eventnametextview.setVisibility(View.GONE);
        roundrect.setVisibility(View.GONE);
        eventrangetextview.setVisibility(View.GONE);
        calendaricon.setVisibility(View.GONE);
        holidaytextview.setVisibility(View.GONE);
        eventfixstextview.setVisibility(View.GONE);

        final View view = new View(this);
        ViewGroup.LayoutParams layoutParams1=new ViewGroup.LayoutParams((int)eventRect.width(),(int)eventRect.height());
        view.setLeft((int) eventRect.left);
        view.setTop((int) eventRect.top);
        view.setRight((int) eventRect.right);
        view.setBottom((int) eventRect.bottom);
        view.setLayoutParams(layoutParams1);



        redlay.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
        layoutParams.height = (int) eventRect.height();
        layoutParams.width = (int) eventRect.width();
        redlay.setLayoutParams(layoutParams);
        redlay.setTranslationX(eventRect.left);
        redlay.setTranslationY(eventRect.top + toolbar.getHeight());

       if (event.getColor()!=0){
           GradientDrawable shape =  new GradientDrawable();
           shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
           mycolor=event.getColor();
           shape.setColor(mycolor);
           redlay.setBackground(shape);
           roundrect.setBackground(shape);

       }
       else {
           GradientDrawable shape =  new GradientDrawable();
           shape.setCornerRadius( getResources().getDimensionPixelSize(R.dimen.fourdp) );
           mycolor=Color.parseColor("#009688");
           shape.setColor(mycolor);
           redlay.setBackground(shape);
           roundrect.setBackground(shape);


       }

        //  GradientDrawable drawable = (GradientDrawable) holder.eventtextview.getBackground();

//               if (eventalllist.get(position).getType()==0)drawable.setColor(eventalllist.get(position).getColor());
//               else drawable.setColor(Color.BLACK);
        redlay.setTranslationZ(0);

        ValueAnimator animwidth = ValueAnimator.ofInt(redlay.getWidth(), getDevicewidth());
        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.width = val;
                redlay.setLayoutParams(layoutParams);
            }
        });
        animwidth.setDuration(300);

        ValueAnimator animheight = ValueAnimator.ofInt(redlay.getHeight(), getDeviceHeight());
        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.height = val;
                redlay.setLayoutParams(layoutParams);
                if (redlay.getTranslationZ() == 0 && valueAnimator.getAnimatedFraction() > 0.2) {
                    redlay.setBackgroundColor(Color.WHITE);
                    shadow.setVisibility(View.VISIBLE);
                    redlay.setTranslationZ(getResources().getDimensionPixelSize(R.dimen.tendp));
                }
            }
        });
        animheight.setDuration(300);

        ValueAnimator animx = ValueAnimator.ofFloat(redlay.getTranslationX(), 0);
        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                redlay.setTranslationX(val);
            }
        });
        animx.setDuration(300);

        ValueAnimator animy = ValueAnimator.ofFloat(redlay.getTranslationY(), 0);
        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                redlay.setTranslationY(val);
            }
        });
        animy.setDuration(300);

        animheight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closebtn.setVisibility(View.VISIBLE);
                        eventnametextview.setVisibility(View.VISIBLE);
                        roundrect.setVisibility(View.VISIBLE);
                        eventrangetextview.setVisibility(View.VISIBLE);
                        calendaricon.setVisibility(View.VISIBLE);
                        holidaytextview.setVisibility(View.VISIBLE);
                        eventfixstextview.setVisibility(View.VISIBLE);
                    }
                }, 150);

            }
        });
        animwidth.start();
        animheight.start();
        animy.start();
        animx.start();
        eventview = view;
        fullview = view;
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {


        if (weekviewcontainer.getVisibility() == View.GONE||!isgivepermission) return;
        if (isAppBarClosed()) {

            LocalDate localDate=new LocalDate(newFirstVisibleDay.get(Calendar.YEAR),newFirstVisibleDay.get(Calendar.MONTH)+1,newFirstVisibleDay.get(Calendar.DAY_OF_MONTH));
            MainActivity.lastdate=localDate;

            String year = localDate.getYear() == LocalDate.now().getYear() ? "" : localDate.getYear() + "";
            if (!monthname.getText().equals(localDate.toString("MMM") + " " + year)){
                MainActivity.lastdate=localDate;
                calendarView.setCurrentmonth(localDate);
                calendarView.adjustheight();
                mIsExpanded = false;
                mAppBar.setExpanded(false, false);

                String transYear = localDate.toString("MMM") + " " + year;
                if (transYear.contains("Jan")) {
                    monthname.setText("MŨGAA "+ year);
                }
                if (transYear.contains("Feb")) {
                    monthname.setText("MŨGETHO  " + year);
                }
                if (transYear.contains("Mar")) {
                    monthname.setText("KĨHU "+ year);
                }
                if (transYear.contains("Apr")) {
                    monthname.setText("MŨTHATŨ "+ year);
                }
                if (transYear.contains("May")) {
                    monthname.setText("MŨGIRA NJARA "+ year);
                }
                if (transYear.contains("Jun")) {
                    monthname.setText("GATHATHANWA "+ year);
                }
                if (transYear.contains("Jul")) {
                    monthname.setText("GATHANO "+ year);
                }
                if (transYear.contains("Aug")) {
                    monthname.setText("MUORIA NYONI  "+ year);
                }
                if (transYear.contains("Sep")) {
                    monthname.setText("MŨG Wa Keeri "+ year);
                }
                if (transYear.contains("Oct")) {
                    monthname.setText("MWANIA THENGE "+year);
                }
                if (transYear.contains("Nov")) {
                    monthname.setText("KANYUA HŨNGŨ "+ year);
                }
                if (transYear.contains("Dec")) {
                    monthname.setText("GATUMU "+ year);
                }

                //first translation here
                Log.d(TAG, "onFirstVisibleDayChanged: WEEEEEEWRR 111"+localDate.toString("MMM"));
                Log.d(TAG, "onFirstVisibleDayChanged: WEEEEEEWRR 222"+ year);
                Log.d(TAG, "onFirstVisibleDayChanged: WEEEEEEWRR 333"+localDate.toString("MMM") + " " + year);
            }

            // EventBus.getDefault().post(new MessageEvent(new LocalDate(monthModel.getYear(),monthModel.getMonth(),1)));
            // if (monthChangeListner!=null)monthChangeListner.onmonthChange(myPagerAdapter.monthModels.get(position));
        } else {
            // calendarView.setCurrentmonth(i);
        }
    }
}
