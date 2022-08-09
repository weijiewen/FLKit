package com.wjw.flkitexample;

import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.wjw.flkit.FLTableView;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkitexample.databinding.ActivityDialogBinding;

import java.util.ArrayList;

public class DialogActivity extends FLBaseActivity<ActivityDialogBinding> {
    private ArrayList<String> strings = new ArrayList();
    @Override
    protected ActivityDialogBinding creatBinding() {
        return ActivityDialogBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void didLoad() {
        navigationView.setTitle("加载loading");
        strings.add("loading加载");
        strings.add("tip提示");
        strings.add("loading后提示");
        strings.add("提示时loading");
        strings.add("dialog-alert单选项弹窗");
        strings.add("dialog-alert多选项弹窗");
        strings.add("dialog-actionSheet标题内容弹窗");
        strings.add("dialog-actionSheet单标题弹窗");
        strings.add("dialog-actionSheet单内容弹窗");
        strings.add("dialog-actionSheet无标题内容弹窗");
        FLTableView.DataSource<DialogCell> dataSource = new FLTableView.DataSource<DialogCell>() {
            @Override
            public int itemCount() {
                return strings.size();
            }

            @Override
            public int itemType(int index) {
                return 0;
            }

            @Override
            public int getItemLayout(int itemType) {
                return R.layout.cell_main;
            }

            @Override
            public DialogCell createItem(View itemView, int viewType) {
                return new DialogCell(itemView);
            }

            @Override
            public void bindItem(DialogCell view, int index) {
                view.bindData(index, strings.get(index));
            }
        };
        binding.tableView.setDataSource(dataSource);
        binding.tableView.reloadData(true);
    }

    @Override
    protected void didClick(View view) {

    }

    private class DialogCell extends FLTableView.FLTableViewCell<String> {

        public DialogCell(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void configItem() {
            String tip = "塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发";
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (itemIndex) {
                        case 0:
                            showLoading();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dismissLoading();
                                }
                            }, 1000);
                            break;
                        case 1:
                            showTip(tip);
                            break;
                        case 2:
                            showLoading();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showTip(tip);
                                }
                            }, 1000);
                            break;
                        case 3:
                            showTip(tip);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showLoading();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoading();
                                        }
                                    }, 1200);
                                }
                            }, 1000);
                            break;
                        case 4:
                            showDialogAlert("测试", tip, FLDialogStyle.Alert, new FLAlertDialogConfig() {
                                @Override
                                public void addItems(FLAlertDialog dialog) {
                                    dialog.addCancel(new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击取消");
                                        }
                                    });
                                    dialog.addItem("确认", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击确认");
                                        }
                                    });
                                }
                            });
                            break;
                        case 5:
                            showDialogAlert("测试", tip, FLDialogStyle.Alert, new FLAlertDialogConfig() {
                                @Override
                                public void addItems(FLAlertDialog dialog) {
                                    dialog.addCancel(new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击取消");
                                        }
                                    });
                                    dialog.addItem("确认", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击确认");
                                        }
                                    });
                                    dialog.addItem("测试", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击测试");
                                        }
                                    });
                                }
                            });
                            break;
                        case 6:
                            showDialogAlert("测试", tip, FLDialogStyle.ActionSheet, new FLAlertDialogConfig() {
                                @Override
                                public void addItems(FLAlertDialog dialog) {
                                    dialog.addCancel(new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击取消");
                                        }
                                    });
                                    dialog.addItem("确认", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击确认");
                                        }
                                    });
                                    dialog.addItem("测试", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击测试");
                                        }
                                    });
                                }
                            });
                            break;
                        case 7:
                            showDialogAlert("测试", "", FLDialogStyle.ActionSheet, new FLAlertDialogConfig() {
                                @Override
                                public void addItems(FLAlertDialog dialog) {
                                    dialog.addCancel(new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击取消");
                                        }
                                    });
                                    dialog.addItem("确认", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击确认");
                                        }
                                    });
                                    dialog.addItem("测试", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击测试");
                                        }
                                    });
                                }
                            });
                            break;
                        case 8:
                            showDialogAlert("", tip, FLDialogStyle.ActionSheet, new FLAlertDialogConfig() {
                                @Override
                                public void addItems(FLAlertDialog dialog) {
                                    dialog.addCancel(new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击取消");
                                        }
                                    });
                                    dialog.addItem("确认", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击确认");
                                        }
                                    });
                                    dialog.addItem("测试", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击测试");
                                        }
                                    });
                                }
                            });
                            break;
                        case 9:
                            showDialogAlert("", "", FLDialogStyle.ActionSheet, new FLAlertDialogConfig() {
                                @Override
                                public void addItems(FLAlertDialog dialog) {
                                    dialog.addCancel(new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击取消");
                                        }
                                    });
                                    dialog.addItem("确认", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击确认");
                                        }
                                    });
                                    dialog.addItem("测试", new FLAlertDialogTouch() {
                                        @Override
                                        public void touch() {
                                            showTip("点击测试");
                                        }
                                    });
                                }
                            });
                            break;
                    }
                }
            });
        }

        @Override
        protected void dataUpdated(String oldData) {
            setText(R.id.text, itemData);
        }
    }
}