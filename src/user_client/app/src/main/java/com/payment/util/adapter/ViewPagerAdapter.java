package com.payment.util.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.payment.R;
import com.payment.databinding.ViewpagerItemBinding;
import com.payment.model.Method;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ADD_CARD_VIEW_TYPE = 1;
    private static final int CARD_VIEW_VIEW_TYPE = 2;
    private static final int CARD_NUM_LENGTH = 16;
    private static final String TAG = "ViewPagerAdapter";

    private List<Method> mItems;
    private boolean imageCheck = true;
    private String payMethodNum;
    private String payMethodType;
    private int currentPosition;


    public ViewPagerAdapter(List<Method> list){
        mItems = list;
        mItems.add(null);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ADD_CARD_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_addcard, parent, false);
            return new AddCardViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_item, parent, false);
            return new CardViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == CARD_VIEW_VIEW_TYPE){
            ((CardViewHolder)holder).binding.setCardViewModel(mItems.get(position));
            setCardColor(holder, mItems.get(position).getBankName());
            currentPosition = position;
            if (mItems.get(position).getPaymentMethodNum().length() == CARD_NUM_LENGTH){
                makeCardNumFormat(holder, mItems.get(position).getPaymentMethodNum());
            }else{
                ((CardViewHolder)holder).binding.cardPaymentMethodNumTextView.setText(mItems.get(position).getPaymentMethodNum());
            }

            ((CardViewHolder)holder).binding.cardSelectedImageView.setOnClickListener(v -> {
                if (!imageCheck){
                    ((CardViewHolder)holder).binding.cardSelectedImageView.setImageResource(R.drawable.not_checked_24dp);
                    imageCheck = true;
                }else{
                    ((CardViewHolder)holder).binding.cardSelectedImageView.setImageResource(R.drawable.on_checked_24dp);
                    payMethodNum = mItems.get(position).getPaymentMethodNum();
                    payMethodType = mItems.get(position).getPaymentMethodType();
                    Log.e(TAG,""+payMethodType+" , "+payMethodNum);
                    imageCheck = false;
                }
            });
        }
    }

    public int getCurrentPosition(){
        return currentPosition;
    }


    private void makeCardNumFormat(RecyclerView.ViewHolder holder, String cardNumbers){
        if (cardNumbers.length() == CARD_NUM_LENGTH){
            String maskingStr = "****";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(cardNumbers.substring(0,4));
            stringBuilder.append(maskingStr);
            stringBuilder.append(maskingStr);
            stringBuilder.append(cardNumbers.substring(12,16));
            stringBuilder.insert(4, "    ");
            stringBuilder.insert(12, "    ");
            stringBuilder.insert(20, "    ");

            ((CardViewHolder)holder).binding.cardPaymentMethodNumTextView.setText(stringBuilder.toString());
        }
    }

    private void setCardColor(RecyclerView.ViewHolder holder, String cardName){
        switch (cardName){
            case "신한":
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.shinhan);
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.ShinHan_card);
                break;
            case "삼성":
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.samsung);
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.SamSung_card);
                break;
            case "현대":
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.hyundai);
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.HyunDai_card);
                break;
            case "BC":
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.bc);
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.BC_card);
                break;
            case "KB국민":
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.KB_card);
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.kb);
                ((CardViewHolder)holder).binding.paymentTypeTextView.setTextColor(Color.BLACK);
                ((CardViewHolder)holder).binding.cardPaymentMethodNumTextView.setTextColor(Color.BLACK);
                break;
            case "하나":
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.hana);
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.Hana_card);
                break;
            case "롯데":
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.lotte);
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.Lotte_card);
                break;
            case "농협":
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.nh);
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.NH_card);
                break;
            case "시티":
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.citi);
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.Citi_bank);
                break;
            case "카카오뱅크":
                ((CardViewHolder)holder).binding.cardFormLayout.setBackgroundResource(R.color.Kakao_bank);
                ((CardViewHolder)holder).binding.cardBrandImageView.setImageResource(R.drawable.kakao);
                ((CardViewHolder)holder).binding.paymentTypeTextView.setTextColor(Color.DKGRAY);
                ((CardViewHolder)holder).binding.cardPaymentMethodNumTextView.setTextColor(Color.BLACK);
                break;
        }
    }

    public String getPayMethodNum(){
        return payMethodNum;
    }

    public String getPayMethodType(){
        return payMethodType;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mItems.size() - 1) {
            return ADD_CARD_VIEW_TYPE;
        } else {
            return CARD_VIEW_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if (mItems == null)
            return 0;
        return mItems.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        ViewpagerItemBinding binding;
        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    class AddCardViewHolder extends RecyclerView.ViewHolder {
        AddCardViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
