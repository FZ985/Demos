package com.demos.merge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.annotation.DrawableRes;

import com.demos.Logger;
import com.demos.utils.ExecutorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * by JFZ
 * 2024/12/15
 * desc：图片地图点
 **/
public class MapPointHelper {


    private static MapPointHelper helper;

    //不透明点
    private final List<int[]> opaquePoints = new ArrayList<>();

    //透明点
    private final List<int[]> transparentPoints = new ArrayList<>();

    //边缘点
    private final List<int[]> edgePoints = new ArrayList<>();


    private MapPointHelper() {

    }

    public static MapPointHelper getInstance() {
        if (helper == null) {
            synchronized (MapPointHelper.class) {
                if (helper == null) {
                    helper = new MapPointHelper();
                }
            }
        }
        return helper;
    }


    public void init(Context context, @DrawableRes int resId) {
        init(context, resId, null);
    }

    public void init(Context context, @DrawableRes int resId, OnLoadCallback callback) {
        ExecutorHelper.getInstance().diskIO().execute(() -> {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            opaquePoints.clear();
            opaquePoints.addAll(getOpaquePoints(bitmap));

            transparentPoints.clear();
            transparentPoints.addAll(getTransparentPoints(bitmap));

            edgePoints.clear();
            edgePoints.addAll(getEdgePoints(bitmap));

            Logger.e("图片点：opa:" + opaquePoints.size() + ",trans:" + transparentPoints.size() + ",edge:" + edgePoints.size());
            if (callback != null) {
                ExecutorHelper.getInstance().mainThread().execute(callback::onLoaded);
            }
        });
    }

    public List<int[]> getOpaquePoints() {
        return opaquePoints;
    }

    public List<int[]> getTransparentPoints() {
        return transparentPoints;
    }

    public List<int[]> getEdgePoints() {
        return edgePoints;
    }

    public boolean isNotEmpty() {
        return opaquePoints.size() != 0;
    }

    public int[] getRandomPoint() {
        Random random = new Random();
        int randomIndex = random.nextInt(opaquePoints.size());
        return opaquePoints.get(randomIndex);
    }

    /**
     * 获取 PNG 图片中所有透明点的坐标
     *
     * @param bitmap 要解析的 Bitmap
     * @return 透明点坐标列表
     */
    private List<int[]> getTransparentPoints(Bitmap bitmap) {
        List<int[]> transparentPoints = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                if (Color.alpha(pixel) == 0) { // 完全透明
                    transparentPoints.add(new int[]{x, y});
                }
            }
        }
        return transparentPoints;
    }

    /**
     * 获取 PNG 图片中所有非透明点的坐标
     *
     * @param bitmap 要解析的 Bitmap
     * @return 非透明点坐标列表
     */
    private List<int[]> getOpaquePoints(Bitmap bitmap) {
        List<int[]> opaquePoints = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                if (Color.alpha(pixel) != 0) { // 非透明
                    opaquePoints.add(new int[]{x, y});
                }
            }
        }
        return opaquePoints;
    }

    /**
     * 获取非透明点的边缘点
     *
     * @param bitmap 要解析的 Bitmap
     * @return 边缘点的坐标列表
     */
    public List<int[]> getEdgePoints(Bitmap bitmap) {
        List<int[]> edgePoints = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                if (Color.alpha(pixel) != 0 && isEdge(bitmap, x, y)) {
                    edgePoints.add(new int[]{x, y});
                }
            }
        }
        return edgePoints;
    }

    /**
     * 判断当前像素是否是边缘点
     *
     * @param bitmap 要解析的 Bitmap
     * @param x      像素的横坐标
     * @param y      像素的纵坐标
     * @return 是否为边缘点
     */
    private boolean isEdge(Bitmap bitmap, int x, int y) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 遍历当前像素的四周
        int[] dx = {-1, 1, 0, 0}; // 左右
        int[] dy = {0, 0, -1, 1}; // 上下
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            // 检查相邻点是否在边界内
            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                int neighborPixel = bitmap.getPixel(nx, ny);
                if (Color.alpha(neighborPixel) == 0) {
                    return true; // 相邻点是透明的，当前点为边缘点
                }
            } else {
                // 如果超出边界，也视为边缘点
                return true;
            }
        }
        return false; // 四周没有透明点，当前点不是边缘点
    }


    public interface OnLoadCallback {

        void onLoaded();
    }
}
