package com.vivo.globalsearchdemo.view;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivo.globalsearchdemo.MyActivityManager;
import com.vivo.globalsearchdemo.R;
import com.vivo.globalsearchdemo.model.ContactBean;
import com.vivo.globalsearchdemo.model.LocalAppBean;
import com.vivo.globalsearchdemo.model.MessageBean;
import com.vivo.globalsearchdemo.presenter.PresenterImpl;

import java.util.ArrayList;
import java.util.List;

public class ResultItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private final int TYPE_APP = 0;
    private final int TYPE_CONTACT = 1;
    private final int TYPE_MESSAGE = 2;
    private final int TYPE_BAIDU = 3;


    static private boolean contactIsDeployed;
    static private boolean appIsDeployed;
    static private boolean messageIsDeployed;
    static private boolean baiduIsDeployed;


    private List<LocalAppBean> mApps;
    private List<LocalAppBean> mShowApps;

    private List<MessageBean> mMessages;
    private List<MessageBean> mShowMessages;

    private List<String> mBaidus;
    private List<String> mShowBaidus;

    private List<ContactBean> mContacts;
    private List<ContactBean> mShowContacts;

    private PresenterImpl presenter;
    private String searchString;
    private Context mContext;

    public static String input;


    ResultItemAdapter(Context context) {
        mApps = new ArrayList<>();
        mShowApps = new ArrayList<>();
        mBaidus = new ArrayList<>();
        mShowBaidus = new ArrayList<>();
        mShowContacts = new ArrayList<>();
        mContacts = new ArrayList<>();
        mMessages = new ArrayList<>();
        mShowMessages = new ArrayList<>();
        mContext = context;
        presenter = new PresenterImpl(mContext);
        contactIsDeployed = false;
        appIsDeployed = false;
        baiduIsDeployed = false;
        messageIsDeployed = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_APP:
                final AppViewHolder appHolder = new AppViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_result_item, parent, false));
                appHolder.contentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LocalAppBean appClicked = mShowApps.get(appHolder.getAdapterPosition());

                        Intent intent = new Intent();
                        ComponentName cn = new ComponentName(appClicked.getPack(), appClicked.getAction());
                        intent.setComponent(cn);
                        mContext.startActivity(intent);

                        saveHistory();
                        MyActivityManager.finishAll();
                    }
                });
                appHolder.contentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", mShowApps.get(appHolder.getAdapterPosition()).getPack(), null));
                        mContext.startActivity(intent);
                        saveHistory();

                        return true;
                    }
                });
                appHolder.deploy.setOnClickListener(this);
                return appHolder;


            case TYPE_CONTACT:
                final ContactViewHolder contactHolder = new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_result_item, parent, false));
                contactHolder.contactContentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(mShowContacts.get(contactHolder.getAdapterPosition() - mShowApps.size()).getId()));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        mContext.startActivity(intent);
                    }
                });
                contactHolder.messageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        Uri uri = Uri.parse("smsto:" + mShowContacts.get(contactHolder.getAdapterPosition() - mShowApps.size()).getNumber());
                        intent.setData(uri);
                        mContext.startActivity(intent);
                        saveHistory();
                    }
                });
                contactHolder.callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri uri = Uri.parse("tel:" + mShowContacts.get(contactHolder.getAdapterPosition() - mShowApps.size()).getNumber());
                        intent.setData(uri);
                        mContext.startActivity(intent);
                        saveHistory();

                    }
                });
                contactHolder.contactDeploy.setOnClickListener(this);
                return contactHolder;

            case TYPE_MESSAGE:
                final MessageViewHolder messageViewHolder = new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_result_item, parent, false));
                messageViewHolder.messageContentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        ComponentName comp = new ComponentName("com.android.mms", "com.android.mms.ui.ConversationList");
//                        intent.setComponent(comp);
//                        intent.setAction("android.intent.action.VIEW");
//                        intent.putExtra("_id", String.valueOf(mShowMessages.get(messageViewHolder.getAdapterPosition() - mShowApps.size() - mShowContacts.size()).getId()));
//                        //						intent.setData(Uri.parse("content://telephony/sms/conversations/" + String.valueOf(mShowMessages.get(messageViewHolder.getAdapterPosition() - mShowApps.size() - mShowContacts.size()).getId())));

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setType("vnd.android-dir/mms-sms");
                        intent.setData(Uri.parse("content://mms-sms/conversations/"+String.valueOf(mShowMessages.get(messageViewHolder.getAdapterPosition() - mShowApps.size() - mShowContacts.size()).getNumber())));//
                        mContext.startActivity(intent);
                        saveHistory();
                        MyActivityManager.finishAll();
                    }
                });

                messageViewHolder.messageDeploy.setOnClickListener(this);
                return messageViewHolder;

            case TYPE_BAIDU:
                final BaiduViewHolder baiduViewHolder = new BaiduViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.baidu_result_item, parent, false));
                baiduViewHolder.baiduContentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.baidu.com/s?ie=UTF-8&wd=" + mShowBaidus.get(baiduViewHolder.getAdapterPosition() - mShowApps.size() - mShowContacts.size() - mShowMessages.size())));
                        mContext.startActivity(intent);
                        saveHistory();
                        MyActivityManager.finishAll();
                    }
                });

                baiduViewHolder.baiduDeploy.setOnClickListener(this);
                return baiduViewHolder;


            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //如果是appItem
        if (holder instanceof AppViewHolder) {
            //设置图标与应用名
            ((AppViewHolder) holder).icon.setImageDrawable(mShowApps.get(position).getIcon());
            ((AppViewHolder) holder).name.setText(mShowApps.get(position).getName());

            //设置标签出现
            if (position == 0) {
                ((AppViewHolder) holder).appLabelLayout.setVisibility(View.VISIBLE);
                ((AppViewHolder) holder).deploy.setVisibility(View.VISIBLE);
                if (mApps.size() < 4) {
                    ((AppViewHolder) holder).deploy.setVisibility(View.GONE);
                }
                holder.itemView.setTag(0);
            } else {
                ((AppViewHolder) holder).appLabelLayout.setVisibility(View.GONE);
            }

            //设置标签文字
            if (position == 0) {
                if (!appIsDeployed) {
                    ((AppViewHolder) holder).deploy.setText("展开");
                } else {
                    ((AppViewHolder) holder).deploy.setText("折叠");
                }
            }

            //设置各行分界线
            if (position != mShowApps.size() - 1 && mShowApps.size() != 1) {
                ((AppViewHolder) holder).divideLine.setVisibility(View.VISIBLE);
                holder.itemView.setTag(1);
            } else {
                ((AppViewHolder) holder).divideLine.setVisibility(View.INVISIBLE);
            }
        }


        //如果是contactItem
        if (holder instanceof ContactViewHolder) {
            //设置标签出现
            if (position == mShowApps.size()) {
                ((ContactViewHolder) holder).contactLabelLayout.setVisibility(View.VISIBLE);
                ((ContactViewHolder) holder).contactDeploy.setVisibility(View.VISIBLE);
                if (mContacts.size() < 4) {
                    ((ContactViewHolder) holder).contactDeploy.setVisibility(View.GONE);
                }
                holder.itemView.setTag(0);
            } else {
                ((ContactViewHolder) holder).contactLabelLayout.setVisibility(View.GONE);
            }

//			设置标签出现
//			if (position == mShowApps.size()) {
//				((ContactViewHolder) holder).contactLabelLayout.setVisibility(View.VISIBLE);
//				((ContactViewHolder) holder).contactDeploy.setVisibility(View.VISIBLE);
//				if (mContacts.size() < 4) {
//					((ContactViewHolder) holder).contactDeploy.setVisibility(View.GONE);
//				}
//			}

            //设置标签文字
            if (position == mShowApps.size()) {
                if (!contactIsDeployed) {
                    ((ContactViewHolder) holder).contactDeploy.setText("展开");
                } else {
                    ((ContactViewHolder) holder).contactDeploy.setText("折叠");
                }
            }

            //设置各行分界线
            if (position != mShowApps.size() + mShowContacts.size() - 1 && mShowContacts.size() != 1) {
                ((ContactViewHolder) holder).contactDivideLine.setVisibility(View.VISIBLE);
            } else {
                ((ContactViewHolder) holder).contactDivideLine.setVisibility(View.INVISIBLE);
            }

            //设置Item显示姓名
            ((ContactViewHolder) holder).name.setText(mShowContacts.get(position - mShowApps.size()).getName());
        }


        //如果是messageItem
        if (holder instanceof MessageViewHolder) {

            String text = mShowMessages.get(position - mShowApps.size() - mShowContacts.size()).getContent();

            String big = input.length() >= text.length() ? input : text;
            String small = input.length() >= text.length() ? text : input;

            int k = 0;
            int i1 = 0;
            int j1 = 0;
            String common = "";
            for (int i = 0; i < small.length(); i++) {
                for (int j = i + 1; j <= small.length(); j++) {
                    if (big.contains(small.substring(i, j)) && j - i > k) {
                        common = small.substring(i, j);
                        k = j - i;
                        i1 = text.indexOf(common);
                        j1 = i1 + common.length();
                    }
                }
            }


            if (i1 > 7 && text.length() > 20) {
                if (text.length() - i1 < 7) {
                    text = "..." + text.substring(text.length() - 20, text.length());
                    i1 = text.indexOf(common);
                    j1 = i1 + common.length();
                } else {
                    text = "..." + text.substring(i1 - 7, text.length());
                    i1 = text.indexOf(common);
                    j1 = i1 + common.length();
                    if (text.length() > 20) {
                        text = text.substring(0, 20) + "...";
                    }
                }
            }
            if (i1 <= 7 && text.length() > 20) {
                text = text.substring(0, 20) + "...";
            }



                SpannableStringBuilder styled = new SpannableStringBuilder(text);
            styled.setSpan(new ForegroundColorSpan(0xFF66CFFF), i1, j1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((MessageViewHolder) holder).name.setText(styled);


            //设置联系人
            if (mShowMessages.get(position - mShowApps.size() - mShowContacts.size()).getContact().equals("empty"))
                ((MessageViewHolder) holder).contactName.setText(mShowMessages.get(position - mShowApps.size() - mShowContacts.size()).getNumber());
            else
                ((MessageViewHolder) holder).contactName.setText(mShowMessages.get(position - mShowApps.size() - mShowContacts.size()).getContact());


            //设置标签出现
            if (position == mShowApps.size() + mShowContacts.size()) {
                ((MessageViewHolder) holder).messageLabelLayout.setVisibility(View.VISIBLE);
                ((MessageViewHolder) holder).messageDeploy.setVisibility(View.VISIBLE);
                if (mMessages.size() < 4) {
                    ((MessageViewHolder) holder).messageDeploy.setVisibility(View.GONE);
                }
                holder.itemView.setTag(0);
            } else {
                ((MessageViewHolder) holder).messageLabelLayout.setVisibility(View.GONE);
            }

            //设置标签文字
            if (position == mShowApps.size() + mShowContacts.size()) {
                if (!messageIsDeployed) {
                    ((MessageViewHolder) holder).messageDeploy.setText("展开");
                } else {
                    ((MessageViewHolder) holder).messageDeploy.setText("折叠");
                }
            }

            //设置各行分界线
            if (position != mShowApps.size() + mShowContacts.size() + mShowMessages.size() - 1 && mShowMessages.size() != 1) {
                ((MessageViewHolder) holder).messageDivideLine.setVisibility(View.VISIBLE);
                holder.itemView.setTag(1);
            } else {
                ((MessageViewHolder) holder).messageDivideLine.setVisibility(View.INVISIBLE);
            }
        }
        //如果是baiduItem
        if (holder instanceof BaiduViewHolder) {

            ((BaiduViewHolder) holder).name.setText(mShowBaidus.get(position - mShowApps.size() - mShowContacts.size() - mShowMessages.size()));

            //设置标签出现
            if (position == mShowApps.size() + mShowContacts.size() + mShowMessages.size()) {
                ((BaiduViewHolder) holder).baiduLabelLayout.setVisibility(View.VISIBLE);
                ((BaiduViewHolder) holder).baiduDeploy.setVisibility(View.VISIBLE);
                if (mBaidus.size() < 4) {
                    ((BaiduViewHolder) holder).baiduDeploy.setVisibility(View.GONE);
                }
                holder.itemView.setTag(0);
            } else {
                ((BaiduViewHolder) holder).baiduLabelLayout.setVisibility(View.GONE);
            }

            //设置标签文字
            if (position == mShowApps.size() + mShowContacts.size() + mShowMessages.size()) {
                if (!baiduIsDeployed) {
                    ((BaiduViewHolder) holder).baiduDeploy.setText("展开");
                } else {
                    ((BaiduViewHolder) holder).baiduDeploy.setText("折叠");
                }
            }

            //设置各行分界线
            if (position != mShowApps.size() + mShowContacts.size() + mShowMessages.size() + mShowBaidus.size() - 1 && mShowBaidus.size() != 1) {
                ((BaiduViewHolder) holder).baiduDivideLine.setVisibility(View.VISIBLE);
                holder.itemView.setTag(1);
            } else {
                ((BaiduViewHolder) holder).baiduDivideLine.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mShowApps.size() + mShowContacts.size() + mShowBaidus.size() + mShowMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mShowApps.size())
            return TYPE_APP;
        else if (position < mShowApps.size() + mShowContacts.size())
            return TYPE_CONTACT;
        else if (position < mShowApps.size() + mShowContacts.size() + mShowMessages.size())
            return TYPE_MESSAGE;
        else return TYPE_BAIDU;
    }

    public void clearAllItems() {
        mApps.clear();
        mContacts.clear();
        mBaidus.clear();
        mMessages.clear();
    }

    public void addAppViewItems(List<LocalAppBean> newList) {
        mApps.clear();
        mApps.addAll(newList);
    }


    public void addContactViewItems(List<ContactBean> newList) {
        mContacts.clear();
        mContacts.addAll(newList);
    }

    public void addBaiduViewItems(List<String> newList) {
        mBaidus.clear();
        mBaidus.addAll(newList);
    }

    public void addMessageViewItems(List<MessageBean> newList) {
        mMessages.clear();
        mMessages.addAll(newList);
    }

    public void updateItems() {
        contactIsDeployed = false;
        appIsDeployed = false;
        baiduIsDeployed = false;
        messageIsDeployed = false;
        mShowContacts.clear();
        for (int i = 0; i < mContacts.size() && i <= 2; i++) {
            mShowContacts.add(mContacts.get(i));
        }

        mShowApps.clear();
        for (int i = 0; i < mApps.size() && i <= 2; i++) {
            mShowApps.add(mApps.get(i));
        }
        mShowBaidus.clear();
        for (int i = 0; i < mBaidus.size() && i <= 2; i++) {
            mShowBaidus.add(mBaidus.get(i));
        }
        mShowMessages.clear();
        for (int i = 0; i < mMessages.size() && i <= 2; i++) {
            mShowMessages.add(mMessages.get(i));
        }
        this.notifyDataSetChanged();

    }

    private void extendAppItems() {
        mShowApps.clear();
        mShowApps.addAll(mApps);
        this.notifyDataSetChanged();
    }


    private void extendContactItems() {
        mShowContacts.clear();
        mShowContacts.addAll(mContacts);
        this.notifyDataSetChanged();
    }

    private void extendBaiduItems() {
        mShowBaidus.clear();
        mShowBaidus.addAll(mBaidus);
        this.notifyDataSetChanged();
    }

    private void extendMessageItems() {
        mShowMessages.clear();
        mShowMessages.addAll(mMessages);
        this.notifyDataSetChanged();
    }

    private void collapseAppItems() {
        mShowApps.clear();
        for (int i = 0; i < mApps.size() && i <= 2; i++) {
            mShowApps.add(mApps.get(i));
        }
        this.notifyDataSetChanged();
    }

    private void collapseContactItems() {
        mShowContacts.clear();
        for (int i = 0; i < mContacts.size() && i <= 2; i++) {
            mShowContacts.add(mContacts.get(i));
        }
        this.notifyDataSetChanged();

    }

    private void collapseBaiduItems() {
        mShowBaidus.clear();
        for (int i = 0; i < mBaidus.size() && i <= 2; i++) {
            mShowBaidus.add(mBaidus.get(i));
        }
        this.notifyDataSetChanged();
    }

    private void collapseMessageItems() {
        mShowMessages.clear();
        for (int i = 0; i < mMessages.size() && i <= 2; i++) {
            mShowMessages.add(mMessages.get(i));
        }
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.app_deploy:
                if (!appIsDeployed) {
                    extendAppItems();
                    appIsDeployed = true;

                } else {
                    collapseAppItems();
                    appIsDeployed = false;
                }
                notifyDataSetChanged();
                break;
            case R.id.baidu_deploy:
                if (!baiduIsDeployed) {
                    extendBaiduItems();
                    baiduIsDeployed = true;

                } else {
                    collapseBaiduItems();
                    baiduIsDeployed = false;
                }
                notifyDataSetChanged();
                break;
            case R.id.contact_deploy:
                if (!contactIsDeployed) {
                    extendContactItems();
                    contactIsDeployed = true;
                } else {
                    collapseContactItems();
                    contactIsDeployed = false;
                }
                notifyDataSetChanged();
                break;
            case R.id.message_deploy:
                if (!messageIsDeployed) {
                    extendMessageItems();
                    messageIsDeployed = true;
                } else {
                    collapseMessageItems();
                    messageIsDeployed = false;
                }
                notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    void setSearchString(String s) {
        searchString = s;
    }

    private void saveHistory() {
        String REGEX1 = "\\s*";
        if (!searchString.matches(REGEX1)) {
            String REGEX2 = "\\s+.*";
            String REGEX3 = ".*\\s+";
            //去掉首尾空格
            if (searchString.matches(REGEX2) || searchString.matches(REGEX3)) {
                searchString = searchString.replaceAll("\\s+", "");
            }
            presenter.saveHist(searchString);
        }
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        RelativeLayout appLabelLayout;
        LinearLayout contentLayout;
        TextView deploy;
        FrameLayout divideLine;

        private AppViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.app_item_icon);
            name = itemView.findViewById(R.id.app_item_name);
            appLabelLayout = itemView.findViewById(R.id.app_result_label);
            contentLayout = itemView.findViewById(R.id.app_result_content);
            deploy = itemView.findViewById(R.id.app_deploy);
            divideLine = itemView.findViewById(R.id.divide_line);
        }

    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        RelativeLayout contactLabelLayout;
        FrameLayout contactDivideLine;
        LinearLayout contactContentLayout;
        FrameLayout callButton;
        FrameLayout messageButton;
        TextView contactDeploy;


        private ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_item_name);
            contactLabelLayout = itemView.findViewById(R.id.contact_result_label);
            contactDivideLine = itemView.findViewById(R.id.contact_divide_line);
            contactContentLayout = itemView.findViewById(R.id.contact_result_content);
            callButton = itemView.findViewById(R.id.call_button);
            messageButton = itemView.findViewById(R.id.message_button);
            contactDeploy = itemView.findViewById(R.id.contact_deploy);
        }

    }

    static class BaiduViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        RelativeLayout baiduLabelLayout;
        FrameLayout baiduDivideLine;
        LinearLayout baiduContentLayout;
        TextView baiduDeploy;


        private BaiduViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.baidu_item_name);
            baiduLabelLayout = itemView.findViewById(R.id.baidu_result_label);
            baiduDivideLine = itemView.findViewById(R.id.baidu_divide_line);
            baiduContentLayout = itemView.findViewById(R.id.baidu_result_content);
            baiduDeploy = itemView.findViewById(R.id.baidu_deploy);
        }

    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView contactName;
        RelativeLayout messageLabelLayout;
        FrameLayout messageDivideLine;
        LinearLayout messageContentLayout;
        TextView messageDeploy;


        private MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.message_item_contact);
            name = itemView.findViewById(R.id.message_item_name);
            messageLabelLayout = itemView.findViewById(R.id.message_result_label);
            messageDivideLine = itemView.findViewById(R.id.message_divide_line);
            messageContentLayout = itemView.findViewById(R.id.message_result_content);
            messageDeploy = itemView.findViewById(R.id.message_deploy);
        }

    }
}
