package com.wjw.flkitexample.pages.base.activities;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjw.flkit.ui.FLTableView;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLBindingActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.ui.FLTableView.FLBindingCell;
import com.wjw.flkitexample.databinding.ActivityDialogBinding;
import com.wjw.flkitexample.databinding.CellMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogActivity extends FLBindingActivity<ActivityDialogBinding> {
    private List<String> datas = new ArrayList<>();
    @Override
    protected ActivityDialogBinding getBinding() {
        return ActivityDialogBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("dialog弹窗");
    }

    @Override
    protected void didLoad() {
        datas = Arrays.asList(
                "dialog-alert单选项弹窗",
                "dialog-alert多选项弹窗",
                "dialog-actionSheet标题内容弹窗",
                "dialog-actionSheet单标题弹窗",
                "dialog-actionSheet单内容弹窗",
                "dialog-actionSheet无标题内容弹窗",
                "dialog-actionSheet无标题内容无取消弹窗"
        );
        FLTableView.CreatCell<DialogCell> creatCell = new FLTableView.CreatCell<DialogCell>() {
            @Override
            public int itemCount(int section) {
                return datas.size();
            }

            @Override
            public DialogCell getCell(@NonNull ViewGroup parent) {
                return new DialogCell(CellMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
        };
        binding.tableView.setCreatCell(creatCell);
        binding.tableView.reloadData(true);
    }

    @Override
    protected void didClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class DialogCell extends FLBindingCell<CellMainBinding> {
        public DialogCell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
        }

        @Override
        protected void bindData(int section, int index) {
            cellBinding.text.setText(datas.get(index));
        }

        @Override
        protected void onClick(int section, int index) {
            super.onClick(section, index);
            String tip = "塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发";
            switch (index) {
                case 0:
                    //"dialog-alert单选项弹窗"
                    showDialogAlert(FLBaseActivity.FLDialogStyle.Alert, "测试", "测试弹窗", new FLBaseActivity.FLAlertDialogConfig() {
                        @Override
                        public void addItems(FLBaseActivity.FLAlertDialog dialog) {
                            dialog.addCancel(new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击取消");
                                }
                            });
                            dialog.addItem("确认", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击确认");
                                }
                            });
                        }
                    });
                    break;
                case 1:
                    //"dialog-alert多选项弹窗"
                    showDialogAlert(FLBaseActivity.FLDialogStyle.Alert, "测试", tip, new FLBaseActivity.FLAlertDialogConfig() {
                        @Override
                        public void addItems(FLBaseActivity.FLAlertDialog dialog) {
                            dialog.addCancel(new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击取消");
                                }
                            });
                            dialog.addItem("确认", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击确认");
                                }
                            });
                            dialog.addItem("测试", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击测试");
                                }
                            });
                        }
                    });
                    break;
                case 2:
                    //"dialog-actionSheet标题内容弹窗"
                    showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, "测试", tip, new FLBaseActivity.FLAlertDialogConfig() {
                        @Override
                        public void addItems(FLBaseActivity.FLAlertDialog dialog) {
                            dialog.addCancel(new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击取消");
                                }
                            });
                            dialog.addItem("确认", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击确认");
                                }
                            });
                            dialog.addItem("测试", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击测试");
                                }
                            });
                        }
                    });
                    break;
                case 3:
                    //"dialog-actionSheet单标题弹窗"
                    showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, "测试", null, new FLBaseActivity.FLAlertDialogConfig() {
                        @Override
                        public void addItems(FLBaseActivity.FLAlertDialog dialog) {
                            dialog.addCancel(new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击取消");
                                }
                            });
                            dialog.addItem("确认", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击确认");
                                }
                            });
                            dialog.addItem("测试", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击测试");
                                }
                            });
                        }
                    });
                    break;
                case 4:
                    //"dialog-actionSheet单内容弹窗"
                    showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, null, tip, new FLBaseActivity.FLAlertDialogConfig() {
                        @Override
                        public void addItems(FLBaseActivity.FLAlertDialog dialog) {
                            dialog.addCancel(new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击取消");
                                }
                            });
                            dialog.addItem("确认", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击确认");
                                }
                            });
                            dialog.addItem("测试", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击测试");
                                }
                            });
                        }
                    });
                    break;
                case 5:
                    //"dialog-actionSheet无标题内容弹窗"
                    showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, null, null, new FLBaseActivity.FLAlertDialogConfig() {
                        @Override
                        public void addItems(FLBaseActivity.FLAlertDialog dialog) {
                            dialog.addCancel(new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击取消");
                                }
                            });
                            dialog.addItem("确认", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击确认");
                                }
                            });
                            dialog.addItem("测试", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击测试");
                                }
                            });
                        }
                    });
                    break;
                case 6:
                    //"dialog-actionSheet无标题内容无取消弹窗"
                    showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, null, null, new FLBaseActivity.FLAlertDialogConfig() {
                        @Override
                        public void addItems(FLBaseActivity.FLAlertDialog dialog) {
                            dialog.addItem("确认", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击确认");
                                }
                            });
                            dialog.addItem("测试", new FLBaseActivity.FLAlertDialogTouch() {
                                @Override
                                public void touch() {
                                    getActivity().showTip("点击测试");
                                }
                            });
                        }
                    });
                    break;
            }
        }
    }
}