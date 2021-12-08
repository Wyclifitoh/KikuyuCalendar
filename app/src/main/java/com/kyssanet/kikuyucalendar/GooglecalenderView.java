package com.kyssanet.kikuyucalendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.kyssanet.kikuyucalendar.MonthChangeListner;

public class GooglecalenderView extends LinearLayout {
    private static final String TAG = "GoogleCalendarView";
    private Context context;
    private ViewPager2 viewPager;
    private MonthChangeListner monthChangeListner;
    private int currentmonth = 0;
    private LocalDate mindate, maxdate;
    private HashMap<LocalDate, EventInfo> eventuser = new HashMap<>();
    private int mDefaultEventColor = Color.parseColor("#9fc6e7");
    public GooglecalenderView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.viewpagerlay, this);
        this.context = context;


    }

    public GooglecalenderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.viewpagerlay, this);
        this.context = context;


    }

    public GooglecalenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.viewpagerlay, this);
        this.context = context;


    }

    public GooglecalenderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.viewpagerlay, this);
        this.context = context;


    }

    public void setMonthChangeListner(MonthChangeListner monthChangeListner) {
        this.monthChangeListner = monthChangeListner;
    }

    public int calculateCurrentMonth(LocalDate currentmonthda) {
        if (currentmonthda == null||mindate==null) return 0;
        LocalDate mindateobj = mindate.toDateTimeAtStartOfDay().dayOfMonth().withMinimumValue().toLocalDate();
        LocalDate current = currentmonthda.dayOfMonth().withMaximumValue();
        int months = Months.monthsBetween(mindateobj, current).getMonths();
        return months;
    }

    public int getCurrentmonth() {
        return currentmonth;
    }

    public void setCurrentmonth(LocalDate currentmonthda) {
        if (mindate==null)return;
        currentmonth = calculateCurrentMonth(currentmonthda);
        if (viewPager.getCurrentItem() != currentmonth) {
            viewPager.setCurrentItem(currentmonth, false);
            //  viewPager.getAdapter().notifyDataSetChanged();
        }
        updategrid();
    }

    public void setCurrentmonth(int position) {

        currentmonth = position;
        if (viewPager.getCurrentItem() != currentmonth) {
            viewPager.setCurrentItem(currentmonth, false);
            //  viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    public void init(HashMap<LocalDate, EventInfo> eventhashmap, LocalDate mindate, LocalDate maxdate) {
        eventuser = new HashMap<>(eventhashmap);
        viewPager = findViewById(R.id.viewpager);

        this.mindate = mindate;
        this.maxdate = maxdate;
        DateTime mindateobj = mindate.toDateTimeAtStartOfDay();
        DateTime maxdateobj = maxdate.toDateTimeAtStartOfDay();
        int months = Months.monthsBetween(mindateobj, maxdateobj).getMonths();

        final ArrayList<MonthModel> arrayList = new ArrayList<>();
        HashMap<LocalDate, EventInfo> eventhash = new HashMap<>();

        for (int i = 0; i <= months; i++) {
            int firstday = mindateobj.dayOfMonth().withMinimumValue().dayOfWeek().get();
            if (firstday == 7) firstday = 0;
            int lastday = mindateobj.dayOfMonth().withMaximumValue().dayOfWeek().get();
            MonthModel month = new MonthModel();
            month.setMonthnamestr(mindateobj.toString("MMMM"));
            month.setMonth(mindateobj.getMonthOfYear());
            month.setNoofday(mindateobj.dayOfMonth().getMaximumValue());
            month.setYear(mindateobj.getYear());
            month.setFirstday(firstday);
            int currentyear = new LocalDate().getYear();
            ArrayList<DayModel> dayModelArrayList = new ArrayList<>();
            DateTime startday = mindateobj.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
            LocalDate minweek = startday.dayOfWeek().withMinimumValue().toLocalDate().minusDays(1);
            while (minweek.compareTo(startday.dayOfMonth().withMaximumValue().toLocalDate()) < 0) {
                if (minweek.getMonthOfYear() == minweek.plusDays(6).getMonthOfYear()) {
                    String lastpattern = minweek.getYear() == currentyear ? "d MMM" : "d MMM YYYY";

                    String s[] = {"tojigs" + minweek.toString("d").toUpperCase() + " - " + minweek.plusDays(6).toString(lastpattern).toUpperCase()};

                    if (!eventhash.containsKey(minweek)) eventhash.put(minweek, new EventInfo(s));

                    minweek = minweek.plusWeeks(1);

                } else {

                    String lastpattern = minweek.getYear() == currentyear ? "d MMM" : "d MMM YYYY";
                    String s[] = {"tojigs" + minweek.toString("d MMM").toUpperCase() + " - " + minweek.plusDays(6).toString(lastpattern).toUpperCase()};


                    if (!eventhash.containsKey(minweek)) eventhash.put(minweek, new EventInfo(s));

                    minweek = minweek.plusWeeks(1);
                }


            }

            for (int j = 1; j <= month.getNoofday(); j++) {

                DayModel dayModel = new DayModel();
                dayModel.setDay(startday.getDayOfMonth());
                dayModel.setMonth(startday.getMonthOfYear());
                dayModel.setYear(startday.getYear());
                if (eventuser.containsKey(startday.toLocalDate())) {
                    if (eventhash.containsKey(startday.toLocalDate())) {
                        EventInfo eventInfo=eventhash.get(startday.toLocalDate());
                        List<String> list = Arrays.asList(eventInfo.eventtitles);
                        list = new ArrayList<>(list);
                        for (String s : eventuser.get(startday.toLocalDate()).eventtitles) {
                            list.add(s);
                        }
                        String[] mStringArray = new String[list.size()];
                        String[] s = list.toArray(mStringArray);
                        eventInfo.eventtitles=s;
                        eventhash.put(startday.toLocalDate(), eventInfo);

                    } else {
                        eventhash.put(startday.toLocalDate(), eventuser.get(startday.toLocalDate()));
                    }
                    // dayModel.setEvents(eventuser.get(startday.toLocalDate()));
                    dayModel.setEventlist(true);

                }

                if (startday.toLocalDate().equals(new LocalDate())) {
                    dayModel.setToday(true);
                    currentmonth = i;
                } else {
                    dayModel.setToday(false);
                }
                dayModelArrayList.add(dayModel);

                if (j == 1) {
                    EventInfo eventInfo1=new EventInfo();

                    String s[] = {"start"};
                    eventInfo1.eventtitles=s;
//                  if (eventhash.containsKey(startday.dayOfWeek().withMinimumValue().toLocalDate())&&eventhash.get(startday.dayOfWeek().withMinimumValue().toLocalDate())[0].contains("tojigs")){
//                     Log.e("remove",startday.dayOfWeek().withMinimumValue().toLocalDate()+"->"+Arrays.asList(eventhash.get(startday.dayOfWeek().withMinimumValue().toLocalDate())));
//                    eventhash.remove(startday.dayOfWeek().withMinimumValue().toLocalDate());

//                  }
                    if (eventhash.containsKey(startday.toLocalDate())) {
                        EventInfo eventInfo=eventhash.get(startday.toLocalDate());
                        List<String> list = Arrays.asList(eventInfo.eventtitles);
                        list = new ArrayList<>(list);
                        list.add(0, "start");
                        String[] mStringArray = new String[list.size()];
                        s = list.toArray(mStringArray);
                        eventInfo.eventtitles=s;
                        eventInfo1=eventInfo;

                    }

                    eventhash.put(startday.toLocalDate(), eventInfo1);
                }
//              if (j==month.getNoofday()&&i!=months){
//                  Log.e("endcount",startday.toLocalDate().toString());
//                  Log.e("end",eventhash.containsKey(startday.toLocalDate())+"");
//                  String s[]={"end"};
//                  eventhash.put(startday.toLocalDate(),s);
//              }
                startday = startday.plusDays(1);

            }
            month.setDayModelArrayList(dayModelArrayList);
            arrayList.add(month);
            mindateobj = mindateobj.plusMonths(1);

        }


        final MonthPagerAdapter myPagerAdapter = new MonthPagerAdapter(context, arrayList);

        viewPager.setAdapter(myPagerAdapter);
//       viewPager.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
//           @Override
//           public void onViewAttachedToWindow(View view) {
//               viewPager.setCurrentItem(currentmonth);
//
//           }
//
//           @Override
//           public void onViewDetachedFromWindow(View view) {
//
//           }
//       });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                MainActivity mainActivity = (MainActivity) context;
                currentmonth = position;

                if (!mainActivity.isAppBarClosed()) {

                    adjustheight();
                    if (mainActivity.mNestedView.getVisibility()==VISIBLE)EventBus.getDefault().post(new MessageEvent(new LocalDate(myPagerAdapter.monthModels.get(position).getYear(), myPagerAdapter.monthModels.get(position).getMonth(), 1)));
                    else {
                        MainActivity.lastdate=new LocalDate(myPagerAdapter.monthModels.get(position).getYear(), myPagerAdapter.monthModels.get(position).getMonth(), 1);
                    }

                    updategrid();
                    //     myPagerAdapter.getFirstFragments().get(position).updategrid();
                    // myPagerAdapter.notifyDataSetChanged();

                    if (monthChangeListner != null)
                        monthChangeListner.onmonthChange(myPagerAdapter.monthModels.get(position));
                }

            }
        });
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                MainActivity mainActivity= (MainActivity) context;
//                currentmonth=position;
//                if (!mainActivity.isAppBarClosed()){
//                    Log.e("onPageSelected","Googlecalendaraview");
//                    adjustheight();
//                    EventBus.getDefault().post(new MessageEvent(new LocalDate(myPagerAdapter.monthModels.get(position).getYear(),myPagerAdapter.monthModels.get(position).getMonth(),1)));
//                    myPagerAdapter.getFirstFragments().get(position).updategrid();
//                    if (monthChangeListner!=null)monthChangeListner.onmonthChange(myPagerAdapter.monthModels.get(position));
//
//
//                }
////                if (myPagerAdapter.getFirstFragments().get(position).isVisible()){
////                    myPagerAdapter.getFirstFragments().get(position).updategrid(arrayList.get(position).getDayModelArrayList());
////                }
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
        LocalDate todaydate = LocalDate.now();
        if (!eventhash.containsKey(todaydate)) {

            eventhash.put(todaydate,new EventInfo( new String[]{"todaydate"}));
        } else {

            List<String> list = Arrays.asList(eventhash.get(todaydate).eventtitles);
            list = new ArrayList<>(list);

            boolean b = true;

            //list.add("todaydate");

            String[] mStringArray = new String[list.size()];
            EventInfo eventInfo=eventhash.get(todaydate);
            eventInfo.eventtitles=list.toArray(mStringArray);
            eventhash.put(todaydate, eventInfo);
        }
        Map<LocalDate, EventInfo> treeMap = new TreeMap<LocalDate, EventInfo>(eventhash);
        HashMap<LocalDate, Integer> indextrack = new HashMap<>();
        int i = 0;
        ArrayList<EventModel> eventModelslist = new ArrayList<>();
        for (HashMap.Entry<LocalDate, EventInfo> localDateStringEntry : treeMap.entrySet()) {

            EventInfo tempinfo=localDateStringEntry.getValue();
            for (String s : localDateStringEntry.getValue().eventtitles) {

                if (s == null) continue;
                int type = 0;
                if (s.startsWith("todaydate")) type = 2;
                else if (s.equals("start")) type = 1;
                else if (s.startsWith("tojigs")) type = 3;

                if (type == 2 && eventModelslist.get(eventModelslist.size() - 1).getType() == 0 && eventModelslist.get(eventModelslist.size() - 1).getLocalDate().equals(localDateStringEntry.getKey())) {

                } else {
                    if (type == 0 && eventModelslist.size() > 0 && eventModelslist.get(eventModelslist.size() - 1).getType() == 0 && !eventModelslist.get(eventModelslist.size() - 1).getLocalDate().equals(localDateStringEntry.getKey())) {

                        eventModelslist.add(new EventModel("dup", localDateStringEntry.getKey(), 100));
                        // if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                        i++;
                    } else if ((type == 3) && eventModelslist.size() > 0 && eventModelslist.get(eventModelslist.size() - 1).getType() == 0) {
                        eventModelslist.add(new EventModel("dup", eventModelslist.get(eventModelslist.size() - 1).getLocalDate(), 100));
                        //   if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                        i++;
                    } else if ((type == 1) && eventModelslist.size() > 0 && eventModelslist.get(eventModelslist.size() - 1).getType() == 0) {
                        eventModelslist.add(new EventModel("dup", eventModelslist.get(eventModelslist.size() - 1).getLocalDate(), 200));
                        // if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                        i++;
                    } else if (type == 0 && eventModelslist.size() > 0 && (eventModelslist.get(eventModelslist.size() - 1).getType() == 1)) {
                        eventModelslist.add(new EventModel("dup", localDateStringEntry.getKey(), 200));
                        //if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                        i++;
                    } else if (type == 2 && eventModelslist.size() > 0 && eventModelslist.get(eventModelslist.size() - 1).getType() == 0) {
                        eventModelslist.add(new EventModel("dup", eventModelslist.get(eventModelslist.size() - 1).getLocalDate(), 100));
                        //  if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
                        i++;
                    }
                    String ss=s;
                    int color=mDefaultEventColor;
                    if (type==0){
                        EventInfo myinfo=eventhashmap.get(localDateStringEntry.getKey());
                        while (myinfo!=null&&!myinfo.title.equals(s)){
                            myinfo=myinfo.nextnode;
                        }
                        color=myinfo.eventcolor==0?mDefaultEventColor:myinfo.eventcolor;
                        if (myinfo.noofdayevent>1){
                            ss=ss+" ("+localDateStringEntry.getKey().toString("d MMMM")+"-"+localDateStringEntry.getKey().plusDays(myinfo.noofdayevent-1).toString("d MMMM")+")";
                        }
                        else if (myinfo.isallday==false){
                            LocalDateTime start=new LocalDateTime(myinfo.starttime, DateTimeZone.forID(myinfo.timezone));
                            LocalDateTime end=new LocalDateTime(myinfo.endtime, DateTimeZone.forID(myinfo.timezone));
                            String sf=start.toString("a").equals(end.toString("a"))?"":"a";

                            ss=ss+" ("+start.toString("h:mm "+sf+"")+"-"+end.toString("h:mm a")+")";

                        }


                    }
//                    if (noofday>1&&type==0){
//                        LocalDate as=localDateStringEntry.getKey();
//                        for (int jj=0;jj<noofday;jj++){
//                            eventModelslist.add(new EventModel(s, as.plusDays(jj), type));
//                            indextrack.put(as.plusDays(jj).plusDays(1), i);
//                            i++;
//                            eventModelslist.add(new EventModel("dup", eventModelslist.get(eventModelslist.size() - 1).getLocalDate(), 200));
//                            // if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
//                            i++;
//                        }
//                    }
//                    else {
                    EventModel mModel=new EventModel(ss, localDateStringEntry.getKey(), type);
                    mModel.setColor(color);
                        eventModelslist.add(mModel);
                        indextrack.put(localDateStringEntry.getKey(), i);
                        i++;
  //                  }

                }


//               if (type==2){
//                   if (eventModelslist.get(eventModelslist.size()-1).getType()!=0){
//                       eventModelslist.add(new EventModel(s,localDateStringEntry.getKey(),type));
//                       if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
//                       i++;
//                   }
//               }
//               else {
//                   eventModelslist.add(new EventModel(s,localDateStringEntry.getKey(),type));
//                   if (!indextrack.containsKey(localDateStringEntry.getKey()))indextrack.put(localDateStringEntry.getKey(),i);
//                   i++;
//               }


            }
        }
        EventBus.getDefault().post(new AddEvent(eventModelslist, indextrack, arrayList));
    }

    public void updategrid() {
        final MonthPagerAdapter myPagerAdapter = (MonthPagerAdapter) viewPager.getAdapter();
        if (myPagerAdapter != null) {
            final int position = viewPager.getCurrentItem();
            // myPagerAdapter.getFirstFragments().get(position).updategrid();
            RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);


            MonthPagerAdapter.MonthViewHolder monthViewHolder = (MonthPagerAdapter.MonthViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            if (monthViewHolder != null && monthViewHolder.jCalendarMonthTopView != null ) {

                monthViewHolder.jCalendarMonthTopView.requestLayout();
                monthViewHolder.jCalendarMonthTopView.invalidate();

            }
        }
    }

    public void adjustheight() {
        if (mindate==null)return;
        final MonthPagerAdapter myPagerAdapter = (MonthPagerAdapter) viewPager.getAdapter();
        if (myPagerAdapter != null) {
            final int position = viewPager.getCurrentItem();
            int size = myPagerAdapter.monthModels.get(position).getDayModelArrayList().size() + myPagerAdapter.monthModels.get(position).getFirstday();
            int numbercolumn = size % 7 == 0 ? size / 7 : (size / 7) + 1;
            ViewGroup.LayoutParams params = getLayoutParams();
            int setheight = 75 + (context.getResources().getDimensionPixelSize(R.dimen.itemheight) * numbercolumn) + context.getResources().getDimensionPixelSize(R.dimen.tendp) + getStatusBarHeight();
            if (params.height == setheight) return;
            params.height = setheight;
            // params.height=0;//jigs change
            setLayoutParams(params);



        }

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

//    public class MyPagerAdapter extends FragmentStatePagerAdapter {
//        private ArrayList<MonthModel> monthModels;
//        private ArrayList<FirstFragment> firstFragments = new ArrayList<>();
//
//        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<MonthModel> monthModels) {
//
//            super(fragmentManager);
//            this.monthModels = monthModels;
//            for (int i = 0; i < monthModels.size(); i++) {
//                firstFragments.add(FirstFragment.newInstance(monthModels.get(i).getMonth(), monthModels.get(i).getYear(), monthModels.get(i).getFirstday(), monthModels.get(i).getDayModelArrayList()));
//            }
//        }
//
//        public ArrayList<MonthModel> getMonthModels() {
//            return monthModels;
//        }
//
//        public ArrayList<FirstFragment> getFirstFragments() {
//            return firstFragments;
//        }
//
//        // Returns total number of pages
//        @Override
//        public int getCount() {
//            return monthModels.size();
//        }
//
//
//        // Returns the fragment to display for that page
//        @Override
//        public Fragment getItem(int position) {
//
//            return firstFragments.get(position);
//
//        }
//    }

    class MonthPagerAdapter extends RecyclerView.Adapter<MonthPagerAdapter.MonthViewHolder> {

        private ArrayList<MonthModel> monthModels;
        private LayoutInflater mInflater;
        private Context context;

        MonthPagerAdapter(Context context, ArrayList<MonthModel> data) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.monthModels = data;
        }

        @NonNull
        @Override
        public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.fraglay, parent, false);
            return new MonthViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {

            MonthModel monthtemp = monthModels.get(position);

            holder.jCalendarMonthTopView.initdata(monthtemp.getDayModelArrayList(), monthtemp.getFirstday(), monthtemp.getMonth(), monthtemp.getYear());
//            Dayadapter dayadapter = new Dayadapter(context, monthtemp.getDayModelArrayList(), monthtemp.getFirstday(), monthtemp.getMonth(), monthtemp.getYear());
//            holder.gridview.setAdapter(dayadapter);
//            dayadapter.notifyDataSetChanged();
            holder.monthnameen.setText(monthModels.get(position).getMonthnamestr());
            int mnth = monthModels.get(position).getMonth();
            switch (mnth) {
                case 1:
                    holder.description.setText("Nĩ hĩndĩ ya riũa ihiũ mũno, mũtĩ wa mũgaa noguo wetiragia.\n" +
                            "Mũgaa is the Acacia Tree. A time when the sun is very hot and only the Acacia tree thrives");
                    break;
                case 2:
                    holder.description.setText("Mũgetho waarĩ mweri wa magetha\n" +
                            "Mũgetho - This is the harvest period");
                    break;
                case 3:
                    holder.description.setText("Matu makoragwo mathimbĩte na magacuha ta me na ihu rĩa mbura yetereirwo ĩitĩke mũthatũ\n" +
                            "This is the month when the clouds are pregnant with rain awaiting to fall next soon");
                    break;
                case 4:
                    holder.description.setText("Igũnyũ cia thaatũ cikoragwo irĩ nyingĩ nĩ ũndũ mahuti nĩ maingĩ na kĩbii onakĩo gĩkoragwo gĩtumanĩte nĩ mbura.\n" +
                            "Green worms are plenty at this time because the leaves are plentiful and the rains are falling");
                    break;
                case 5:
                    holder.description.setText("Mũgĩkũyũ nĩ atithagia, akarũgamia magongona mweri-inĩ ũyũ\n" +
                            "During this period the Mũgĩkũyũ stops performing ceremonies");
                    break;
                case 6:
                    holder.description.setText("GATHATHANWA heho nĩ yambĩrĩria, andũ no kũrigitha moko maigue rugarĩ\n" +
                            "In this period the biting cold has began and people are rubbing their hands for warmth");
                    break;
                case 7:
                    holder.description.setText("GATHANO, nĩ hĩndĩ heho ĩcacĩte, andũ othe manyitĩte thano nĩ heho\n" +
                            "This is the period of extreme cold and everyone is clenching their fists to keep warm ( thano – fist )");
                    break;
                case 8:
                    holder.description.setText("MUORIA NYONI, Nyoni irarĩa kinya ikoria nda nĩ kũhũũna. Kuora nĩ kũbutha\n" +
                            "Birds have eaten beyond their stomachs can hold. Their stomachs are now rotting.");
                    break;
                case 9:
                    holder.description.setText("Mũgaa wa kerĩ, riũa rĩhiũhĩte o ta Mũgaa. Magetha ma keerĩ nĩrĩo magethagwo nĩ ũndũ matingĩbutha nĩ momĩtio nĩ riũa\n" +
                            "This is the second harvest. The sun is as hot as in Mũgaa, the cereals have been dried up by the sun's heat they cannot rot.");
                    break;
                case 10:
                    holder.description.setText("MWANIA THENGE nĩrĩo mũgĩkũyũ aracagia mbũri ciyũire njĩra no kwania\n" +
                            "Mwania thenge is the season for paying dowry. Goats and sheep are \"shouting\" in the roads as they are taken for dowry. ");
                    break;
                case 11:
                    holder.description.setText("KANYUA HŨNGŨ nĩrĩo mũgĩkũyũ athĩnjaga mbũri iria araracĩirio Nyoni ciothe ndĩa nyama irĩ rĩerainĩ nĩ ũndũ nyama nĩ nyingĩ\n" +
                            "KANYUA HŨNGŨ is when the goats and sheep paid as dowry price are being slaughtered, all meat eating birds are flying in the air\n" +
                            "in search of meat as it is in plenty.");
                    break;
                case 12:
                    holder.description.setText("GATUMU nĩrĩo ciũndũ iratumũrwo iria iraracirio, ũguo Gatumu gagatumũrwo\n" +
                            "Gatumu is when the baskets given during dowry are being opened, now what was sealed is being opened.");
                    break;
                default:
                    holder.description.setText("this is the default");
                    break;
            }
            Log.d(TAG, "onBindViewHolder: MMMMNAMEE ENGLISH "+monthModels.get(position).getMonth());
            Log.d(TAG, "onBindViewHolder: MMMMNAMEE ENGLISH MONTH "+monthModels.get(position).getMonthnamestr());
        }

        @Override
        public int getItemCount() {
            return monthModels.size();
        }

        class MonthViewHolder extends RecyclerView.ViewHolder {

           // RecyclerView gridview;
            JCalendarMonthTopView jCalendarMonthTopView;
            TextView monthnameen, description;
            MonthViewHolder(View itemView) {
                super(itemView);
                monthnameen = itemView.findViewById(R.id.monthnameen);
                description = itemView.findViewById(R.id.description);
                jCalendarMonthTopView = itemView.findViewById(R.id.jcalendarmonthview);


            }
        }
    }

    class Dayadapter extends RecyclerView.Adapter<Dayadapter.DayViewHolder> {

        private ArrayList<DayModel> dayModels;
        private LayoutInflater mInflater;
        private int firstday;
        private int month, year;


        public Dayadapter(Context context, ArrayList<DayModel> dayModels, int firstday, int month, int year) {
            this.mInflater = LayoutInflater.from(context);
            this.dayModels = dayModels;
            this.firstday = firstday;
            this.month = month;
            this.year = year;
        }

        @Override
        public Dayadapter.DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = mInflater.inflate(R.layout.gridlay, parent, false);
            return new Dayadapter.DayViewHolder(view);

        }


        @Override
        public void onBindViewHolder(Dayadapter.DayViewHolder holder, int position) {


            if (position >= firstday) {
                position = position - firstday;
                DayModel dayModel = dayModels.get(position);
                boolean selected = dayModel.getDay() == MainActivity.lastdate.getDayOfMonth() && dayModel.getMonth() == MainActivity.lastdate.getMonthOfYear() && dayModel.getYear() == MainActivity.lastdate.getYear() ? true : false;

                if (dayModel.isToday()) {
                    holder.textView.setBackgroundResource(R.drawable.circle);
                    holder.textView.setTextColor(Color.WHITE);

                } else if (selected) {
                    holder.textView.setBackgroundResource(R.drawable.selectedback);
                    holder.textView.setTextColor(Color.rgb(91, 128, 231));

                } else {
                    holder.textView.setBackgroundColor(Color.TRANSPARENT);
                    holder.textView.setTextColor(Color.rgb(80, 80, 80));

                }
                holder.textView.setText(dayModels.get(position).getDay() + "");

                if (dayModel.getEventlist() && !selected) {
                    holder.eventview.setVisibility(View.VISIBLE);
                } else {
                    holder.eventview.setVisibility(View.GONE);
                }
            } else {
                holder.textView.setBackgroundColor(Color.TRANSPARENT);
                holder.textView.setText("");
                holder.eventview.setVisibility(View.GONE);
            }


        }

        @Override
        public int getItemCount() {

            return dayModels.size() + firstday;
        }

        class DayViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;
            private View eventview;

            public DayViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView8);
                eventview = itemView.findViewById(R.id.eventview);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getAdapterPosition() >= firstday) {
                            for (DayModel dayModel : dayModels) {
                                dayModel.setSelected(false);
                            }

                            MainActivity.lastdate = new LocalDate(year, month, dayModels.get(getAdapterPosition() - firstday).getDay());
                            MainActivity mainActivity= (MainActivity) context;
                            if (mainActivity.mNestedView.getVisibility()==VISIBLE)EventBus.getDefault().post(new MessageEvent(new LocalDate(year, month, dayModels.get(getAdapterPosition() - firstday).getDay())));
                            // dayModels.get(getAdapterPosition()-firstday).setSelected(true);
                            if (mainActivity.weekviewcontainer.getVisibility()==VISIBLE){
                                Calendar todaydate=Calendar.getInstance();
                                todaydate.set(Calendar.DAY_OF_MONTH,MainActivity.lastdate.getDayOfMonth());
                                todaydate.set(Calendar.MONTH,MainActivity.lastdate.getMonthOfYear()-1);
                                todaydate.set(Calendar.YEAR,MainActivity.lastdate.getYear());
                                mainActivity.mWeekView.goToDate(todaydate);
                            }
                            notifyDataSetChanged();
                        }

                    }
                });
            }
        }
    }
}
