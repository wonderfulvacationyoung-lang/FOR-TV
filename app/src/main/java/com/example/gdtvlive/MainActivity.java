package com.example.gdtvlive;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends Activity {

    private int currentIndex = 0;
    private boolean dialogShowing = false;

    private String[] channelNames = {
        "广东卫视",
        "珠江频道",
        "体育频道",
        "新闻频道",
        "公共频道",
        "嘉佳卡通",
        "南方卫视",
        "影视频道",
        "少儿频道",
        "房产频道"
    };

    private String[] channelIds = {
        "43", "44", "45", "46", "47", "48", "49", "50", "51", "52"
    };

    private static final String BASE_URL = "https://www.gdtv.cn/tvChannelDetail/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isFirefoxInstalled()) {
            showInstallDialog();
        } else {
            openChannel(0);
        }
    }

    private boolean isFirefoxInstalled() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("org.mozilla.firefox", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void showInstallDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("未检测到 Firefox 浏览器");
        builder.setMessage("本应用需要 Firefox 浏览器才能播放直播。是否现在下载安装？");
        builder.setPositiveButton("去下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.mozilla.org/firefox/android/"));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "请用 U 盘安装 Firefox 68", Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void openChannel(int index) {
        if (index < 0 || index >= channelIds.length) {
            index = 0;
        }
        currentIndex = index;
        String url = BASE_URL + channelIds[index];

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("org.mozilla.firefox");
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Toast.makeText(this, channelNames[index], Toast.LENGTH_SHORT).show();
            // 让 App 退到后台，显示 Firefox
            moveTaskToBack(true);
        } catch (ActivityNotFoundException e) {
            showInstallDialog();
        }
    }

    private void showChannelList() {
        dialogShowing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择频道");
        builder.setItems(channelNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openChannel(which);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogShowing = false;
            }
        });
        dialog.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (dialogShowing) {
            return super.dispatchKeyEvent(event);
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    openChannel(currentIndex - 1);
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    openChannel(currentIndex + 1);
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_MENU:
                    showChannelList();
                    return true;
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                    int num = keyCode - KeyEvent.KEYCODE_0;
                    int target = (num == 0) ? 10 : num;
                    if (target >= 1 && target <= channelIds.length) {
                        openChannel(target - 1);
                    }
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    finish();
                    return true;
                default:
                    return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
