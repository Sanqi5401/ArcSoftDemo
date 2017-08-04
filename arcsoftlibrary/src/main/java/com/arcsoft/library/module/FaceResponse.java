package com.arcsoft.library.module;

import com.arcsoft.facedetection.AFD_FSDKFace;

import java.util.List;

/**
 * Created by Administrator on 2017/8/4.
 */

public class FaceResponse {
    public enum FaceType{
        INIT,
        DETECTION,
        RECOGNITION,
        MATCH
    }
    private int code;
    private FaceType type;
    private List<AFD_FSDKFace> list;
    private byte[] feature;
    private float score;
    private AFD_FSDKFace face;
    private String name;

    public FaceResponse(int code, FaceType type) {
        this.code = code;
        this.type = type;
    }

    public FaceResponse(int code, FaceType type, List<AFD_FSDKFace> list) {
        this.code = code;
        this.type = type;
        this.list = list;
    }

    public FaceResponse(int code, FaceType type, byte[] feature) {
        this.code = code;
        this.type = type;
        this.feature = feature;
    }


    public FaceResponse(int code, FaceType type, float score,AFD_FSDKFace face,String name) {
        this.code = code;
        this.type = type;
        this.score = score;
        this.face = face;
        this.name = name;
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

    public List<AFD_FSDKFace> getList() {
        return list;
    }

    public void setList(List<AFD_FSDKFace> list) {
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

    public AFD_FSDKFace getFace() {
        return face;
    }

    public void setFace(AFD_FSDKFace face) {
        this.face = face;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
