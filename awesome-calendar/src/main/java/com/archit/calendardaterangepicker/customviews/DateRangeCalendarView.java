package com.archit.calendardaterangepicker.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.ViewPager;

import com.archit.calendardaterangepicker.R;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarManager.CalendarRangeType;
import com.archit.calendardaterangepicker.models.CalendarStyleAttrImpl;
import com.archit.calendardaterangepicker.models.CalendarStyleAttributes;
import com.archit.calendardaterangepicker.tools.MonthProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateRangeCalendarView extends LinearLayout implements DateRangeCalendarViewApi {

    private CustomTextView tvYearTitle;
    private AppCompatImageView imgVNavLeft, imgVNavRight;
    private final List<Calendar> monthDataList = new ArrayList<>();
    private AdapterEventCalendarMonths adapterEventCalendarMonths;
    private ViewPager vpCalendar;
    private CalendarStyleAttributes calendarStyleAttr;
    private CalendarListener mCalendarListener;

    private final static int TOTAL_ALLOWED_MONTHS = 30;

    public DateRangeCalendarView(final Context context) {
        super(context);
        initViews(context, null);
    }

    public DateRangeCalendarView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public DateRangeCalendarView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    private void initViews(final Context context, final AttributeSet attrs) {
        calendarStyleAttr = new CalendarStyleAttrImpl(context, attrs);
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_calendar_container, this, true);
        final RelativeLayout rlHeaderCalendar = findViewById(R.id.rlHeaderCalendar);
        rlHeaderCalendar.setBackground(calendarStyleAttr.getHeaderBg());
        tvYearTitle = findViewById(R.id.tvYearTitle);
        tvYearTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarStyleAttr.getTextSizeTitle());
        imgVNavLeft = findViewById(R.id.imgVNavLeft);
        imgVNavRight = findViewById(R.id.imgVNavRight);
        vpCalendar = findViewById(R.id.vpCalendar);

        monthDataList.clear();
        final Calendar today = (Calendar) Calendar.getInstance().clone();
        today.add(Calendar.MONTH, -TOTAL_ALLOWED_MONTHS);

        for (int i = 0; i < TOTAL_ALLOWED_MONTHS * 2; i++) {
            monthDataList.add((Calendar) today.clone());
            today.add(Calendar.MONTH, 1);
        }
        adapterEventCalendarMonths = new AdapterEventCalendarMonths(context, monthDataList, calendarStyleAttr);
        vpCalendar.setAdapter(adapterEventCalendarMonths);
        vpCalendar.setOffscreenPageLimit(0);
        vpCalendar.setCurrentItem(TOTAL_ALLOWED_MONTHS);
        setCalendarYearTitle(TOTAL_ALLOWED_MONTHS);
        setListeners();
    }

    private void setListeners() {

        vpCalendar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                setCalendarYearTitle(position);
                setNavigationHeader(position);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
        imgVNavLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int newPosition = vpCalendar.getCurrentItem() - 1;
                if (newPosition > -1) {
                    vpCalendar.setCurrentItem(newPosition);
                }
            }
        });
        imgVNavRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int newPosition = vpCalendar.getCurrentItem() + 1;
                if (newPosition < monthDataList.size()) {
                    vpCalendar.setCurrentItem(newPosition);
                }
            }
        });
    }


    /**
     * To set navigation header ( Left-Right button )
     *
     * @param position New page position
     */
    private void setNavigationHeader(final int position) {
        imgVNavRight.setVisibility(VISIBLE);
        imgVNavLeft.setVisibility(VISIBLE);
        if (monthDataList.size() == 1) {
            imgVNavLeft.setVisibility(INVISIBLE);
            imgVNavRight.setVisibility(INVISIBLE);
        } else if (position == 0) {
            imgVNavLeft.setVisibility(INVISIBLE);
        } else if (position == monthDataList.size() - 1) {
            imgVNavRight.setVisibility(INVISIBLE);
        }
    }

    /**
     * To set calendar year title
     *
     * @param position data list position for getting date
     */
    private void setCalendarYearTitle(final int position) {
        final Calendar currentCalendarMonth = monthDataList.get(position);
        String uaMonth = new MonthProvider().provide(currentCalendarMonth.get(Calendar.MONTH));
        final String yearTitle = uaMonth + " " + currentCalendarMonth.get(Calendar.YEAR);
        tvYearTitle.setText(yearTitle);
        tvYearTitle.setTextColor(calendarStyleAttr.getTitleColor());
    }

    /**
     * To set calendar listener
     *
     * @param calendarListener Listener
     */
    @Override
    public void setCalendarListener(@NonNull final CalendarListener calendarListener) {
        mCalendarListener = calendarListener;
        adapterEventCalendarMonths.setCalendarListener(mCalendarListener);
    }

    /**
     * To apply custom fonts to all the text views
     *
     * @param fonts - Typeface that you want to apply
     */
    @Override
    public void setFonts(@NonNull final Typeface fonts) {
        tvYearTitle.setTypeface(fonts);
        calendarStyleAttr.setFonts(fonts);
        adapterEventCalendarMonths.invalidateCalendar();
    }

    /**
     * To remove all selection and redraw current calendar
     */
    @Override
    public void resetAllSelectedViews() {
        adapterEventCalendarMonths.resetAllSelectedViews();
    }

    /**
     * To set week offset. To start week from any of the day. Default is 0 (Sunday).
     *
     * @param offset 0-Sun, 1-Mon, 2-Tue, 3-Wed, 4-Thu, 5-Fri, 6-Sat
     */
    @Override
    public void setWeekOffset(final int offset) {
        calendarStyleAttr.setWeekOffset(offset);
        adapterEventCalendarMonths.invalidateCalendar();
    }

    /**
     * To set left navigation ImageView drawable
     */
    @Override
    public void setNavLeftImage(@NonNull final Drawable leftDrawable) {
        imgVNavLeft.setImageDrawable(leftDrawable);
    }

    /**
     * To set right navigation ImageView drawable
     */
    @Override
    public void setNavRightImage(@NonNull final Drawable rightDrawable) {
        imgVNavRight.setImageDrawable(rightDrawable);
    }

    /**
     * Sets start and end date.<br>
     * <B>Note:</B><br>
     * You can not set null start date with valid end date.<br>
     * You can not set end date before start date.<br>
     * If you are setting custom month range than do not call this before calling (@method setVisibleMonthRange).<br>
     *
     * @param startDate Start date
     * @param endDate   End date
     */
    @Override
    public void setSelectedDateRange(@Nullable final Calendar startDate, @Nullable final Calendar endDate) {
        if (startDate == null && endDate != null) {
            throw new IllegalArgumentException("Start date can not be null if you are setting end date.");
        } else if (endDate != null && endDate.before(startDate)) {
            throw new IllegalArgumentException("Start date can not be after end date.");
        }
        adapterEventCalendarMonths.setSelectedDate(startDate, endDate);
    }

    /**
     * To get start date.
     */
    @NonNull
    @Override
    public Calendar getStartDate() {
        return adapterEventCalendarMonths.getMinSelectedDate();
    }

    /**
     * To get end date.
     */
    @NonNull
    @Override
    public Calendar getEndDate() {
        return adapterEventCalendarMonths.getMaxSelectedDate();
    }

    /**
     * To set editable mode. Default value will be true.
     *
     * @param isEditable true if you want user to select date range else false
     */
    @Override
    public void setEditable(final boolean isEditable) {
        adapterEventCalendarMonths.setEditable(isEditable);
    }

    /**
     * To get editable mode.
     */
    @Override
    public boolean isEditable() {
        return adapterEventCalendarMonths.isEditable();
    }

    /**
     * To provide month range to be shown to user. If start month is greater than end month than it will give {@link IllegalArgumentException}.<br>
     * By default it will also make selectable date range as per visible month's dates. If you want to customize the selectable date range then
     * use {@link #setSelectableDateRange(Calendar, Calendar, List)}.<br><br>
     * <b>Note:</b> Do not call this method after calling date selection method {@link #setSelectableDateRange(Calendar, Calendar, List)}
     * / {@link #setSelectedDateRange(Calendar, Calendar)} as it will reset date selection.
     *
     * @param startMonth Start month of the calendar
     * @param endMonth   End month of the calendar
     */
    @Override
    public void setVisibleMonthRange(@NonNull final Calendar startMonth, @NonNull final Calendar endMonth) {

        final Calendar startMonthDate = (Calendar) startMonth.clone();
        final Calendar endMonthDate = (Calendar) endMonth.clone();

        startMonthDate.set(Calendar.DATE, 1);
        CalendarRangeUtils.resetTime(startMonthDate, CalendarRangeType.START_DATE);

        endMonthDate.set(Calendar.DATE, 1);
        CalendarRangeUtils.resetTime(endMonthDate, CalendarRangeType.LAST_DATE);

        if (startMonthDate.after(endMonthDate)) {
            throw new IllegalArgumentException("Start month(" + startMonthDate.getTime().toString() + ") can not be later than end month(" + endMonth.getTime().toString() + ").");
        }
        monthDataList.clear();

        while (!isDateSame(startMonthDate, endMonthDate)) {
            monthDataList.add((Calendar) startMonthDate.clone());
            startMonthDate.add(Calendar.MONTH, 1);
        }
        monthDataList.add((Calendar) startMonthDate.clone());

        adapterEventCalendarMonths = new AdapterEventCalendarMonths(getContext(), monthDataList, calendarStyleAttr);
        vpCalendar.setAdapter(adapterEventCalendarMonths);
        vpCalendar.setOffscreenPageLimit(0);
        vpCalendar.setCurrentItem(0);
        setCalendarYearTitle(0);
        setNavigationHeader(0);
        adapterEventCalendarMonths.setCalendarListener(mCalendarListener);
    }

    /**
     * To set current visible month.
     *
     * @param calendar Month to be set as current
     */
    @Override
    public void setCurrentMonth(@NonNull final Calendar calendar) {
        for (int i = 0; i < monthDataList.size(); i++) {
            final java.util.Calendar month = monthDataList.get(i);
            if (month.get(java.util.Calendar.YEAR) == calendar.get(java.util.Calendar.YEAR)) {
                if (month.get(java.util.Calendar.MONTH) == calendar.get(java.util.Calendar.MONTH)) {
                    vpCalendar.setCurrentItem(i);
                    break;
                }
            }
        }
    }

    @Override
    public void setSelectableDateRange(
            @NonNull Calendar startDate,
            @NonNull Calendar endDate,
            @NonNull List<Long> enabledDates
    ) {
        if (endDate.before(startDate)) {
            String message = "Start date(" +
                    startDate.getTime().toString() +
                    ") can not be after end date(" +
                    endDate.getTime().toString() +
                    ").";
            throw new IllegalArgumentException(message);
        }
        adapterEventCalendarMonths.setSelectableDateRange(startDate, endDate, enabledDates);
    }

    private boolean isDateSame(@NonNull final Calendar one, @NonNull final Calendar second) {
        return one.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && one.get(Calendar.MONTH) == second.get(Calendar.MONTH)
                && one.get(Calendar.DATE) == second.get(Calendar.DATE);
    }

    @Override
    public void setEnabledCircleColor(int enabledCircleColor) {
        adapterEventCalendarMonths.setEnabledCircleColor(enabledCircleColor);
    }

    @Override
    public void setEnabledTextColor(int enabledTextColor) {
        adapterEventCalendarMonths.setEnabledTextColor(enabledTextColor);
    }

}
