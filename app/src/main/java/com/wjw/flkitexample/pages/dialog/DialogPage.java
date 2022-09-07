package com.wjw.flkitexample.pages.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wjw.flkit.FLTableView;
import com.wjw.flkit.base.FLBaseActivity;
import com.wjw.flkit.base.FLNavigationView;
import com.wjw.flkit.base.FLTabBarActivity;
import com.wjw.flkitexample.databinding.CellMainBinding;
import com.wjw.flkitexample.databinding.PageDialogBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogPage extends FLTabBarActivity.FLTabBarPage<PageDialogBinding> {
    private List<String> datas = new ArrayList<>();
    public DialogPage(Context context) {
        super(context);
    }

    @Override
    protected PageDialogBinding getBinding() {
        return PageDialogBinding.inflate(LayoutInflater.from(getContext()), this, false);
    }

    @Override
    protected void configNavigation(FLNavigationView navigationView) {
        navigationView.setTitle("dialog展示");
        navigationView.setBackgroundColor(Color.BLACK);
        navigationView.setForegroundColor(Color.WHITE);
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
    protected void pageWillShow() {
        super.pageWillShow();
        setStatusStyle(FLBaseActivity.StatusStyle.light);
    }

    private class DialogCell extends FLTableView.FLTableViewCell<CellMainBinding> {


        public DialogCell(@NonNull CellMainBinding cellBinding) {
            super(cellBinding);
            String tip = "塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发塞德里克飞机上课了巨额罚款了解放了快速减肥了苦涩就发了苦涩解放啦卡死机而非卢卡斯荆防颗粒撒巨额罚款拉瑟九分裤阿里kg九色鹿开发";
            cellBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (index) {
                        case 0:
                            getActivity().showDialogAlert(FLBaseActivity.FLDialogStyle.Alert, "测试", tip, new FLBaseActivity.FLAlertDialogConfig() {
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
                            getActivity().showDialogAlert(FLBaseActivity.FLDialogStyle.Alert, "测试", tip, new FLBaseActivity.FLAlertDialogConfig() {
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
                            getActivity().showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, "测试", tip, new FLBaseActivity.FLAlertDialogConfig() {
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
                            getActivity().showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, "测试", null, new FLBaseActivity.FLAlertDialogConfig() {
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
                            getActivity().showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, null, tip, new FLBaseActivity.FLAlertDialogConfig() {
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
                            getActivity().showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, null, null, new FLBaseActivity.FLAlertDialogConfig() {
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
                            getActivity().showDialogAlert(FLBaseActivity.FLDialogStyle.ActionSheet, null, null, new FLBaseActivity.FLAlertDialogConfig() {
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
            });
        }

        @Override
        protected void bindData(CellMainBinding cellBinding, int section, int index) {
            cellBinding.text.setText(datas.get(index));
        }
    }
}
