package com.syr.csrg.seclauncher.ui.customLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Space;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shane on 7/27/14.
 */
public class CustomGridLayout extends GridLayout {

    private int[] maxNumItemsPerRow;
    private int itemColumnSpan;

    private int largestMaxNumItemsPerRow;
    private int smallestMaxNumItemsPerRow;

    private int maxNumRows;

    private int numColumns;
    private int numRows;

    private int nextNumRows;

    private int numItems;

    private int maxNumItems;

    private int[] numItemsPerRow;

    private final class LayoutInfo {
        int gridSpanIndex;
        View view;
    }

    List<LayoutInfo> layout;
    List<LayoutInfo> children;

    public CustomGridLayout(Context context) {
        super(context);
        init(null);
    }

    public CustomGridLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public CustomGridLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        maxNumItemsPerRow = new int[] {2, 3, 2};
        maxNumItems = arraySum(maxNumItemsPerRow);
        maxNumRows = maxNumItemsPerRow.length;
        Log.d("init: maxNumRows", Integer.toString(maxNumRows));
        largestMaxNumItemsPerRow = arrayMax(maxNumItemsPerRow);
        smallestMaxNumItemsPerRow = arrayMin(maxNumItemsPerRow);
        Log.d("init: largestMaxNumItemsPerRow", Integer.toString(largestMaxNumItemsPerRow));
        itemColumnSpan = 10;
        numColumns = (itemColumnSpan * largestMaxNumItemsPerRow) + (largestMaxNumItemsPerRow - 1);
        Log.d("init: numColumns", Integer.toString(numColumns));
        numRows = 1;
        numItems = 0;
        numItemsPerRow = new int[maxNumRows];
        arrayClear(numItemsPerRow);
        nextNumRows = 1;
        layout = new ArrayList<LayoutInfo>();
        children = new ArrayList<LayoutInfo>();
        setColumnCount(numColumns);
        setRowCount(numRows);
    }

    @Override
    public void addView(View child, int index) {
        //super.addView(child, index);
        if (numItems < maxNumItems) {
            numItems++;
            build(child, index);
        }
    }

    private void build(View child, int index) {

        super.removeAllViews();

        LayoutInfo newItem = new LayoutInfo();
        newItem.view = child;

        children.add(index, newItem);

        Log.d("children count after", Integer.toString(getChildCount()));

        int[] rowCounts = setRowCounts();

        Log.d("setting row count", Integer.toString(nextNumRows));

        setRowCount(nextNumRows);

        Log.d("new row counts", "...");
        Log.d("row 0", Integer.toString(rowCounts[0]));
        Log.d("row 1", Integer.toString(rowCounts[1]));
        Log.d("row 2", Integer.toString(rowCounts[2]));

        int row = 0;
        Log.d("row", Integer.toString(row));
        int gridSpanIndex = 0;
        int gridIndex = 0;
        int column = 0;
        for (int itemIndex = 0; itemIndex < children.size(); itemIndex++) {

            LayoutInfo item = children.get(itemIndex);

            int leftSpacerSpan = 1;
            if (firstItemInRow(itemIndex, rowCounts, nextNumRows)) {
                if (rowCounts[row] == largestMaxNumItemsPerRow) {
                    leftSpacerSpan = 0;
                }
                else {
                    leftSpacerSpan = (numColumns - (rowCounts[row] * itemColumnSpan) - (rowCounts[row] - 1)) / 2;
                }
            }
            if (leftSpacerSpan > 0) {
                LayoutParams leftSpacerParams = new LayoutParams();
                leftSpacerParams.columnSpec = GridLayout.spec(column, leftSpacerSpan);
                leftSpacerParams.rowSpec = GridLayout.spec(row, 1);
                leftSpacerParams.width = 20 * leftSpacerSpan;
                leftSpacerParams.height = 200;
                leftSpacerParams.setMargins(0, 10, 0, 10);
                Space leftSpacer = new Space(getContext());
                Log.d("build: addView", "Left spacer at: " + Integer.toString(gridSpanIndex) + ", Span: " + Integer.toString(leftSpacerSpan));
                super.addView(leftSpacer, gridSpanIndex, leftSpacerParams);
                column += leftSpacerSpan;
                gridIndex += leftSpacerSpan;
                gridSpanIndex++;
            }

            LayoutParams itemParams = new LayoutParams();
            itemParams.columnSpec = GridLayout.spec(column, itemColumnSpan);
            itemParams.rowSpec = GridLayout.spec(row, 1);
            itemParams.width = 20 * itemColumnSpan;
            itemParams.height = 200;
            itemParams.setMargins(0, 10, 0, 10);
            item.gridSpanIndex = gridSpanIndex;
            Log.d("build: addView", "Item at: " + Integer.toString(gridSpanIndex) + ", Span: " + Integer.toString(itemColumnSpan));
            super.addView(item.view, gridSpanIndex, itemParams);
            column += itemColumnSpan;
            gridIndex += itemColumnSpan;
            gridSpanIndex++;

            if (lastItemInRow(itemIndex, rowCounts, nextNumRows)) {
                if (rowCounts[row] != largestMaxNumItemsPerRow) {
                    LayoutParams rightSpacerParams = new LayoutParams();
                    int rightSpacerSpan = (numColumns - (rowCounts[row] * itemColumnSpan) - (rowCounts[row] - 1)) / 2;
                    rightSpacerParams.columnSpec = GridLayout.spec(column, rightSpacerSpan);
                    rightSpacerParams.rowSpec = GridLayout.spec(row, 1);
                    rightSpacerParams.width = 20 * rightSpacerSpan;
                    rightSpacerParams.height = 200;
                    rightSpacerParams.setMargins(0, 10, 0, 10);
                    Space rightSpacer = new Space(getContext());
                    Log.d("build: addView", "Right spacer at: " + Integer.toString(gridSpanIndex) + ", Span: " + Integer.toString(rightSpacerSpan));
                    super.addView(rightSpacer, gridSpanIndex, rightSpacerParams);
                    column += rightSpacerSpan;
                    gridIndex += rightSpacerSpan;
                    gridSpanIndex++;
                }
                column = 0;
                row++;
                Log.d("row", Integer.toString(row));

            }
        }

        numRows = nextNumRows;
        numItemsPerRow = rowCounts;
    }
    
    private int getNumRows(int numItems) {
        int accumulate = 0;
        for (int row = 0; row < maxNumRows; row++) {
            accumulate += maxNumItemsPerRow[row];
            if (accumulate >= numItems) {
                return row + 1;
            }
        }
        return maxNumRows;
    }

    private boolean firstItemInRow(int itemIndex, int[] numberOfItemsPerRow, int numberOfRows) {
        int accumulate = 0;
        for (int row = 0; row < numberOfRows; row++) {
            if (itemIndex == accumulate) {
                return true;
            }
            accumulate += numberOfItemsPerRow[row];
        }
        return false;
    }

    private boolean lastItemInRow(int itemIndex, int[] numberOfItemsPerRow, int numberOfRows) {
        int accumulate = 0;
        for (int row = 0; row < numberOfRows; row++) {
            accumulate += numberOfItemsPerRow[row];
            if (itemIndex == accumulate - 1) {
                return true;
            }
        }
        return false;
    }

    private int arrayMax(int[] items) {
        int max = items[0];
        for (int i = 1; i < items.length; i++) {
            if (items[i] > max) {
                max = items[i];
            }
        }
        return max;
    }

    private int arrayMin(int[] items) {
        int min = items[0];
        for (int i = 1; i < items.length; i++) {
            if (items[i] < min) {
                min = items[i];
            }
        }
        return min;
    }

    private int[] setRowCounts() {
        int[] rowCounts = new int[maxNumRows];
        arrayClear(rowCounts);

        switch (numItems) {
            case 1:
                nextNumRows = 1;
                rowCounts[0] = 1;
                break;
            case 2:
                nextNumRows = 1;
                rowCounts[0] = 2;
                break;
            case 3:
                nextNumRows = 2;
                rowCounts[0] = 2;
                rowCounts[1] = 1;
                break;
            case 4:
                nextNumRows = 2;
                rowCounts[0] = 2;
                rowCounts[1] = 2;
                break;
            case 5:
                nextNumRows = 2;
                rowCounts[0] = 2;
                rowCounts[1] = 3;
                break;
            case 6:
                nextNumRows = 3;
                rowCounts[0] = 2;
                rowCounts[1] = 2;
                rowCounts[2] = 2;
                break;
            default:
                nextNumRows = 3;
                rowCounts[0] = 2;
                rowCounts[1] = 3;
                rowCounts[2] = 2;
                break;
        }

//        int countRows = 0;
//        int row = 0;
//        int nextSmallest = smallestMaxNumItemsPerRow;
//        int itemsRemaining = numItems;
//        while (itemsRemaining > 0) {
//            if (rowCounts[row] < maxNumItemsPerRow[row] && rowCounts[row] < nextSmallest) {
//                rowCounts[row]++;
//                itemsRemaining--;
//            }
//            else if (row + 1 < numRows && itemsRemaining < maxNumItemsPerRow[row + 1] || itemsRemaining < nextSmallest) {
//                row = 0;
//                nextSmallest++;
//            }
//            else {
//                row++;
//                if (row >= maxNumRows) {
//                    row = 0;
//                    nextSmallest++;
//                }
//            }
//            else if (rowCounts[row] >= nextSmallest) {
////                i += rowCounts[row] - 1;
//                if (row + 1 < numRows && maxNumItemsPerRow[row + 1] >= numItems - i) {
//                    row++;
//                }
//                else {
//                    row = 0;
//                    nextSmallest++;
//                    continue;
//                }
//            }
//            if (row >= maxNumRows) {
//                row = 0;
//                nextSmallest++;
//                continue;
//            }
//        }
//        for (int i = 0; i < rowCounts.length; i++) {
//            if (rowCounts[i] == 0) {
//                break;
//            }
//            countRows++;
//        }
//        nextNumRows = countRows;
        return rowCounts;
    }

    private void arrayClear(int[] items) {
        for (int i = 0; i < items.length; i++) {
            items[i] = 0;
        }
    }

    private int arraySum(int[] items) {
        int sum = 0;
        for (int i = 0; i < items.length; i++) {
            sum += items[i];
        }
        return sum;
    }

    @Override
    public int indexOfChild(View view) {
        for (int i = 0; i < children.size(); i++) {
            LayoutInfo child = children.get(i);
            if (child.view == view) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void removeView(View view) {
        int index = indexOfChild(view);
        if (numItems > 0) {
            numItems--;
            destroy(view, index);
        }
    }

    private void destroy(View child, int index) {

        Log.d("children count before", Integer.toString(getChildCount()));

        int row = numRows - 1;
        for (int itemIndex = children.size() - 1; itemIndex >= 0; itemIndex--) {
            LayoutInfo item = children.get(itemIndex);

            if (lastItemInRow(itemIndex, numItemsPerRow, numRows)) {
                if (numItemsPerRow[row] != largestMaxNumItemsPerRow) {
                    Log.d("build: removeViewAt", Integer.toString(item.gridSpanIndex + 1));
                    super.removeViewAt(item.gridSpanIndex + 1);
                }
            }

            item.view = super.getChildAt(item.gridSpanIndex);
            Log.d("build: removeViewAt", Integer.toString(item.gridSpanIndex));
            super.removeViewAt(item.gridSpanIndex);

            if (firstItemInRow(itemIndex, numItemsPerRow, numRows)) {
                if (numItemsPerRow[row] != largestMaxNumItemsPerRow) {
                    Log.d("build: removeViewAt", Integer.toString(item.gridSpanIndex - 1));
                    super.removeViewAt(item.gridSpanIndex - 1);
                }
                row--;
            }
            else {
                super.removeViewAt(item.gridSpanIndex - 1);
            }
        }


        children.remove(index);

        Log.d("children count after", Integer.toString(getChildCount()));

        int[] rowCounts = setRowCounts();

        Log.d("setting row count", Integer.toString(nextNumRows));

        setRowCount(nextNumRows);

        Log.d("new row counts", "...");
        Log.d("row 0", Integer.toString(rowCounts[0]));
        Log.d("row 1", Integer.toString(rowCounts[1]));
        Log.d("row 2", Integer.toString(rowCounts[2]));

        row = 0;
        Log.d("row", Integer.toString(row));
        int gridSpanIndex = 0;
        int gridIndex = 0;
        int column = 0;
        for (int itemIndex = 0; itemIndex < children.size(); itemIndex++) {

            LayoutInfo item = children.get(itemIndex);

            int leftSpacerSpan = 1;
            if (firstItemInRow(itemIndex, rowCounts, nextNumRows)) {
                if (rowCounts[row] == largestMaxNumItemsPerRow) {
                    leftSpacerSpan = 0;
                }
                else {
                    leftSpacerSpan = (numColumns - (rowCounts[row] * itemColumnSpan) - (rowCounts[row] - 1)) / 2;
                }
            }
            if (leftSpacerSpan > 0) {
                LayoutParams leftSpacerParams = new LayoutParams();
                leftSpacerParams.columnSpec = GridLayout.spec(column, leftSpacerSpan);
                leftSpacerParams.rowSpec = GridLayout.spec(row, 1);
                leftSpacerParams.width = 20 * leftSpacerSpan;
                leftSpacerParams.height = 200;
                leftSpacerParams.setMargins(0, 10, 0, 10);
                Space leftSpacer = new Space(getContext());
                Log.d("build: addView", "Left spacer at: " + Integer.toString(gridSpanIndex) + ", Span: " + Integer.toString(leftSpacerSpan));
                super.addView(leftSpacer, gridSpanIndex, leftSpacerParams);
                column += leftSpacerSpan;
                gridIndex += leftSpacerSpan;
                gridSpanIndex++;
            }

            LayoutParams itemParams = new LayoutParams();
            itemParams.columnSpec = GridLayout.spec(column, itemColumnSpan);
            itemParams.rowSpec = GridLayout.spec(row, 1);
            itemParams.width = 20 * itemColumnSpan;
            itemParams.height = 200;
            itemParams.setMargins(0, 10, 0, 10);
            item.gridSpanIndex = gridSpanIndex;
            Log.d("build: addView", "Item at: " + Integer.toString(gridSpanIndex) + ", Span: " + Integer.toString(itemColumnSpan));
            super.addView(item.view, gridSpanIndex, itemParams);
            column += itemColumnSpan;
            gridIndex += itemColumnSpan;
            gridSpanIndex++;

            if (lastItemInRow(itemIndex, rowCounts, nextNumRows)) {
                if (rowCounts[row] != largestMaxNumItemsPerRow) {
                    LayoutParams rightSpacerParams = new LayoutParams();
                    int rightSpacerSpan = (numColumns - (rowCounts[row] * itemColumnSpan) - (rowCounts[row] - 1)) / 2;
                    rightSpacerParams.columnSpec = GridLayout.spec(column, rightSpacerSpan);
                    rightSpacerParams.rowSpec = GridLayout.spec(row, 1);
                    rightSpacerParams.width = 20 * rightSpacerSpan;
                    rightSpacerParams.height = 200;
                    rightSpacerParams.setMargins(0, 10, 0, 10);
                    Space rightSpacer = new Space(getContext());
                    Log.d("build: addView", "Right spacer at: " + Integer.toString(gridSpanIndex) + ", Span: " + Integer.toString(rightSpacerSpan));
                    super.addView(rightSpacer, gridSpanIndex, rightSpacerParams);
                    column += rightSpacerSpan;
                    gridIndex += rightSpacerSpan;
                    gridSpanIndex++;
                }
                column = 0;
                row++;
                Log.d("row", Integer.toString(row));

            }
        }

        numRows = nextNumRows;
        numItemsPerRow = rowCounts;


    }
}
