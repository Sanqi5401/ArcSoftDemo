package com.arcsoft.library.module;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKFace;

import java.util.List;

/**
 * Created by Administrator on 2017/8/4.
 */

public class FaceResponse<T extends  ArcsoftFace> {
    public enum FaceType{
        INIT,
        DETECTION,
        RECOGNITION,
        MATCH
    }
    private int code;
    private FaceType type;
    private List<T> list;
    private byte[] feature;
    private float score;
    private T face;
    private String name;
    private int orientation;

    public FaceResponse(int code, FaceType type) {
        this.code = code;
        this.type = type;
    }

    public FaceResponse(int code, FaceType type, List<T> list) {
        this.code = code;
        this.type = type;
        this.list = list;
    }


    public FaceResponse(int code, FaceType type, byte[] feature) {
        this.code = code;
        this.type = type;
        this.feature = feature;
    }


    public FaceResponse(int code, FaceType type, float score, T face, String name, int orientation) {
        this.code = code;
        this.type = type;
        this.score = score;
        this.face = face;
        this.name = name;
        this.orientation = orientation;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public FaceType getType() {
        return type;
    }

    public void setType(FaceType type) {
        this.type = type;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public ArcsoftFace getFace() {
        return face;
    }

    public void setFace(T face) {
        this.face = face;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
