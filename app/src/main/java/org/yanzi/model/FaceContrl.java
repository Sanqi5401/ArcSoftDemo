package org.yanzi.model;

import android.hardware.camera2.params.Face;
import android.util.Log;

import org.yanzi.util.FaceManager;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Administrator on 2017/7/28.
 */

public class FaceContrl {
    private static FaceContrl INSTANT = null;
    private FaceCMD cmd = null;

    public FaceContrl() {

    }

    public static FaceContrl getInstant() {
        if (INSTANT == null)
            return INSTANT = new FaceContrl();
        return INSTANT;
    }

    public boolean isNull() {
        if (cmd == null || cmd.getData() == null) {
            return true;
        }
        return false;
    }

    public void startCmd(FaceCMD cmd) {
        this.cmd = cmd;
        cmd.execute();
    }

}
