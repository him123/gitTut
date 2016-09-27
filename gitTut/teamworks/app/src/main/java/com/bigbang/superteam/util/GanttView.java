package com.bigbang.superteam.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.bigbang.superteam.R;
import com.bigbang.superteam.project.GanttChartActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Created by USER 3 on 21/05/2015.
 */
public class GanttView extends View {

    public static int LIMIT = 20;
    Paint paintBg, paintBlue, paintBlack, paintWhite, paintBlackStroke, paintLightGrey;
    int charSize;
    Rect textrects[];
    Rect gridRects[][];
    Rect SubTitles[];
    Rect Titles[];

    int textWidth;
    int totalHrs;
    int raws;
    int days = 0, tasks = 0;
    double workingHours = 0;
    Date minDate, maxDate;
    Bundle bundle;
    int screenWidth, screenHeight, font_size, cell_size;
    ArrayList<point> rectList = new ArrayList<>();
    Context mContext;
    Date CompStartTime = null, CompEndtime = null;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public GanttView(Context context) {
        super(context);
        mContext = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        return true;

    }

    public void setData(Bundle bdl) {
        this.bundle = bdl;
        screenWidth = bundle.getInt("screenWidth");
        screenHeight = bundle.getInt("screenHeight");
        font_size = screenHeight * 2 / 100;
        cell_size = (int) (screenHeight * 3.5 / 100);
        initPaints();
        charSize = (int) paintBg.measureText("X");
        textWidth = charSize * 20;
        //////////////////
        String sTime = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_STARTTIME);
        String eTime = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ENDTIME);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            CompStartTime = simpleDateFormat.parse(sTime);
            CompEndtime = simpleDateFormat.parse(eTime);
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }

        workingHours = Util.getWorkingHours(CompStartTime, CompEndtime);
        workingHours = Math.ceil(workingHours);
        ////////////////////
        if (GanttChartActivity.WorkList.size() > 0) {
            minDate = findMinDate();
            maxDate = findMaxDate();
            tasks = GanttChartActivity.WorkList.size();
        } else {
            Calendar cal = Calendar.getInstance();
            minDate = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, 10);
            maxDate = cal.getTime();
            tasks = 10;
        }

        days = differenceInDays(minDate, maxDate) + 2;

        totalHrs = (int) (days * workingHours);
        if (tasks > LIMIT) raws = (tasks + 2);
        else raws = (LIMIT + 2);
        initRect();
    }

    private void initRect() {
        textrects = new Rect[raws];
        SubTitles = new Rect[totalHrs];
        Titles = new Rect[days];
        gridRects = new Rect[raws - 2][totalHrs];
        initTaskRects();
        initGridRects();
        initTitles();
        initsubTitles();
    }

    private void initTaskRects() {
        for (int i = 0; i < raws; i++) {
            int top = (i * cell_size) + 1;
            int left = 0;
            int right = textWidth;
            int bottom = top + cell_size - 1;
            textrects[i] = new Rect(left, top, right, bottom);
        }
    }

    private void initTitles() {
        int titleWidth = (int) ((cell_size) * workingHours);
        int top = 0;
        int bottom = cell_size - 1;
        int left, right;
        for (int i = 0; i < days; i++) {
            left = (i * titleWidth) + textWidth + 1;
            right = left + titleWidth - 1;
            Titles[i] = new Rect(left, top, right, bottom);
        }
    }

    private void initsubTitles() {
        int cellSize = cell_size;
        int top = cell_size + 1;
        int bottom = top + cell_size - 1;
        int left, right;
        for (int i = 0; i < totalHrs; i++) {
            left = (i * cellSize) + textWidth + 1;
            right = left + cellSize - 1;
            SubTitles[i] = new Rect(left, top, right, bottom);
        }
    }

    private void initGridRects() {
        for (int i = 0; i < raws - 2; i++) {
            int top = (i * cell_size) + (cell_size * 2) + 1;
            int bottom = top + cell_size - 1;
            for (int j = 0; j < totalHrs; j++) {
                int left = j * (cell_size) + textWidth + 1;
                int right = left + cell_size - 1;
                gridRects[i][j] = new Rect(left, top, right, bottom);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);

        canvas.drawColor(Color.WHITE);
        drawDateTitles(canvas);
        drawHourTitles(canvas);
        drawTaskNames(canvas);
        if (GanttChartActivity.WorkList.size() > 0)
            drawGraph(canvas);
        if (GanttChartActivity.WorkList.size() > 0)
            drawDependencies(canvas);
        canvas.restore();
    }

    private void drawDependencies(Canvas canvas) {
        for (int i = 0; i < tasks; i++) {
            String deps[] = GanttChartActivity.WorkList.get(i).getTaskCodeAfter().split(",");
            for (int j = 0; j < deps.length; j++) {
                if (deps[j] != null && !deps[j].equals("null") && !deps[j].equals("0")) {
                    try {
                        int x = getLocalTaskCode(Integer.parseInt(deps[j]));
                        if (x != -1) {
                            if (x != i)
                                drawdeps(x, i, canvas);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private int getLocalTaskCode(int code) {
        if (code > 0) {
            for (int i = 0; i < GanttChartActivity.WorkList.size(); i++) {
                if (GanttChartActivity.WorkList.get(i).getTaskCode() == code)
                    return i;
            }
        }
        return -1;
    }

    private void drawdeps(int fromTask, int toTask, Canvas canvas) {
        int fromRectNo = rectList.get(fromTask).end;
        int toRectNo = rectList.get(toTask).start;
        float startX1 = gridRects[fromTask][fromRectNo].centerX();
        float startY1 = gridRects[fromTask][fromRectNo].centerY();
        float startX2 = gridRects[fromTask][toRectNo].centerX();
        float startY2 = gridRects[fromTask][toRectNo].centerY();
        float startX3 = gridRects[toTask][toRectNo].centerX();
        float startY3 = gridRects[toTask][toRectNo].centerY();

        canvas.drawLine(startX1, startY1, startX2, startY2, paintBlack);
        canvas.drawLine(startX2, startY2, startX3, startY3, paintBlack);
        canvas.drawCircle(gridRects[fromTask][fromRectNo].exactCenterX(),
                gridRects[fromTask][fromRectNo].exactCenterY(),
                cell_size / 4, paintBlack);
        canvas.drawCircle(gridRects[toTask][toRectNo].exactCenterX(),
                gridRects[toTask][toRectNo].exactCenterY(), cell_size / 4,
                paintBlack);
    }

    private void drawGraph(Canvas canvas) {
        for (int i = 0; i < GanttChartActivity.WorkList.size(); i++) {
            Date startDate = convertToDate(Util.utcToLocalTime(GanttChartActivity.WorkList.get(i).getStartDate()));
            Date endDate = convertToDate(Util.utcToLocalTime(GanttChartActivity.WorkList.get(i).getEndDate()));

            long different = endDate.getTime() - startDate.getTime();
            int hrs = (int) TimeUnit.HOURS.convert(different, TimeUnit.MILLISECONDS);
//            Log.e("task" + i, " startDate :" + startDate + " endDate:" + endDate + " minDate:" + minDate);
            boolean flag = false;
            if (startDate.getHours() > CompEndtime.getHours() ||
                    (startDate.getHours() == CompEndtime.getHours() && startDate.getMinutes() > CompEndtime.getMinutes())) //Constant.endTime) {
            {
                startDate.setHours((CompEndtime.getHours()) - 1);
                flag = true;
            }

            if (startDate.getHours() < CompStartTime.getHours() ||
                    (startDate.getHours() == CompStartTime.getHours() && startDate.getMinutes() < CompStartTime.getMinutes())
                    ) {
                startDate.setHours(CompStartTime.getHours());
                flag = true;
            }


            if (endDate.getHours() > CompEndtime.getHours() ||
                    (endDate.getHours() == CompEndtime.getHours() && endDate.getMinutes() > CompEndtime.getMinutes())
                    ) {
                endDate.setHours(CompEndtime.getHours());
                flag = true;
            }

            if (endDate.getHours() < CompStartTime.getHours() ||
                    endDate.getHours() == CompStartTime.getHours() && endDate.getMinutes() > CompStartTime.getMinutes()) {
                endDate.setHours((CompStartTime.getHours()) + 1);
                endDate.setMinutes(0);
                flag = true;
            }

            int fromRectNo = (int) (differenceInDays(minDate, startDate) * workingHours);

//            Log.e("differenceindays:" + i, "diff: " + differenceInDays(minDate, startDate) + " hours :" + hours + "from rectno:" + fromRectNo);
            int diff = differenceInDays(startDate, endDate) + 1;
            int toRectNo = (int) ((diff * workingHours) - 1 + fromRectNo);
            Log.e("differenceindays:" + i, "diff: " + diff + " to rectno:" + fromRectNo);
            Log.e("Before in bracket :", "startDate:" + startDate.getHours() + " end:" + endDate.getHours() + " rect:" + fromRectNo);
            if (startDate.getHours() >= CompStartTime.getHours() && startDate.getHours() <= CompEndtime.getHours()) {
                fromRectNo = fromRectNo + (startDate.getHours() - CompStartTime.getHours());
                Log.e("in bracket :", "startDate:" + startDate.getHours() + " end:" + endDate.getHours() + " rect:" + fromRectNo);
            }
            if (endDate.getHours() < CompEndtime.getHours() && endDate.getHours() > CompStartTime.getHours()) {
                toRectNo = toRectNo - (CompEndtime.getHours() - endDate.getHours());
                if (endDate.getMinutes() > 0)
                    toRectNo = toRectNo + 1;
            }

            point p = new point();
            p.start = fromRectNo;
            p.end = toRectNo;
            rectList.add(p);
            RectF rf = new RectF(gridRects[i][fromRectNo].left,
                    gridRects[i][fromRectNo].top, gridRects[i][toRectNo].right,
                    gridRects[i][toRectNo].bottom);

            if (flag) {
//                drawCentreText("" + hrs, gridRects[i][toRectNo], canvas, paintWhite);
                canvas.drawRoundRect(rf, font_size, font_size, paintLightGrey);
            } else {
                canvas.drawRoundRect(rf, font_size, font_size, paintBlue);
            }
        }
    }

    // Draws Sub Titles
    private void drawHourTitles(Canvas canvas) {
        for (int i = 0; i < totalHrs; i++) {
            canvas.drawRect(SubTitles[i], paintBg);
            drawCentreText("" + (int) (i % workingHours + 1), SubTitles[i], canvas, paintWhite);
        }
    }

    // Draw Titles
    private void drawDateTitles(Canvas canvas) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(minDate);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM");
        for (int i = 0; i < days; i++) {
            canvas.drawRect(Titles[i], paintBg);
            canvas.drawRect(Titles[i].left + 1, Titles[i].top, Titles[i].right, textrects[raws - 1].bottom - 1, paintBlackStroke);
            String str = format.format(cal.getTime());
            drawCentreText(str, Titles[i], canvas, paintWhite);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    // Draws Task texts
    private void drawTaskNames(Canvas canvas) {
        for (int i = 0; i < raws; i++) { // For Blank Rectangles
            canvas.drawRect(textrects[i], paintBg);
        }
        for (int i = 0; i < GanttChartActivity.WorkList.size(); i++) {
            String str = GanttChartActivity.WorkList.get(i).getTitle();
            if (str.length() > 15) str = str.substring(0, 15) + "...";
            drawCentreText(str, textrects[i + 2], canvas, paintWhite);
        }
    }

    private void drawCentreText(String string, Rect rect, Canvas canvas, Paint p) {
        RectF bounds = new RectF(rect);
        // measure text width
        bounds.right = p.measureText(string, 0, string.length());
        // measure text height
        bounds.bottom = p.descent() - p.ascent();
        bounds.left += (rect.width() - bounds.right) / 2.0f;
        bounds.top += (rect.height() - bounds.bottom) / 2.0f;
        canvas.drawText(string, bounds.left, bounds.top - p.ascent(),
                p);
    }

    // Draws Centered Text in the provided rectangle
    // / sets user screen size according to requirements of the tasks and days
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int ScreenWidth = (int) (textWidth + (workingHours * days * cell_size));
        int ScreenHeight = raws * cell_size;
        setMeasuredDimension(ScreenWidth, ScreenHeight);
    }

    private Date findMaxDate() {
        Date maxDate = convertToDate(GanttChartActivity.WorkList.get(0).getEndDate());
        for (int i = 0; i < GanttChartActivity.WorkList.size(); i++) {
            if (convertToDate(GanttChartActivity.WorkList.get(i).getEndDate()).after(maxDate))
                maxDate = convertToDate(GanttChartActivity.WorkList.get(i).getEndDate());
        }
        return maxDate;
    }

    private Date findMinDate() {
        Date minDate = convertToDate(GanttChartActivity.WorkList.get(0).getStartDate());
        for (int i = 0; i < GanttChartActivity.WorkList.size(); i++) {
            if (convertToDate(GanttChartActivity.WorkList.get(i).getStartDate()).before(minDate))
                minDate = convertToDate(GanttChartActivity.WorkList.get(i).getStartDate());
        }
        minDate.setHours(CompStartTime.getHours());
        minDate.setMinutes(0);
        return minDate;
    }

    // Initializes Paint Obj
    private void initPaints() {
        paintBg = new Paint();
        paintBg.setColor(mContext.getResources().getColor(R.color.btnColor));
        paintBlue = new Paint();
        paintBlue.setStyle(Paint.Style.FILL);
        paintBlue.setColor(Color.BLUE);
        // border
        paintBlack = new Paint();
        paintBlack.setColor(Color.BLACK);
        paintBlack.setTextSize(font_size);

        paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setTextSize(font_size * 0.7f);

        paintBlackStroke = new Paint();
        paintBlackStroke.setColor(Color.BLACK);
        paintBlackStroke.setStyle(Paint.Style.STROKE);

        paintLightGrey = new Paint();
        paintLightGrey.setColor(Color.LTGRAY);
    }

    public int differenceInDays(Date fromDate, Date endDate) {
        if (fromDate.getMonth() == endDate.getMonth()) {
//            Log.e("dates diff", "from date :" + fromDate + " todate :" + endDate + " end:" + endDate.getDate() + " from:" + fromDate.getDate());
            return endDate.getDate() - fromDate.getDate();
        }
        long different = endDate.getTime() - fromDate.getTime();
        return (int) TimeUnit.DAYS.convert(different, TimeUnit.MILLISECONDS);
    }

    public Date convertToDate(String dateString) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = sdf1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    class point {
        int start;
        int end;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }
}
