package com.example.stocklookup;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {
    Context context;
    ArrayList<Stock> stocks;
    ArrayList<Stock> stocksFilterable;
    RecyclerAdapter(Context context,ArrayList<Stock> list)
    {
        this.context=context;
        this.stocks =list;
        this.stocksFilterable=list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View Stockcard= LayoutInflater.from(context).inflate(R.layout.stockcard,parent,false);
        return new ViewHolder(Stockcard);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.stockImg.setImageResource(list.get(position).img);
        holder.stockDes.setText(stocks.get(position).Des);
        holder.stockCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(context);
                dialog.setContentView(R.layout.stockdetails);
                ArrayList<String> details=new ArrayList<>();
                TextView Name=dialog.findViewById(R.id.stockName);
                TextView LastPrice=dialog.findViewById(R.id.stockLastPrice);
                TextView PercentChange=dialog.findViewById(R.id.stockPercentChange);
                ProgressBar progressBar=dialog.findViewById(R.id.progress_circular);
//                ImageView image=dialog.findViewById(R.id.stockImage);
//                Name.setText(list.get(holder.getAdapterPosition()).Des);
//                image.setImageResource(list.get(holder.getAdapterPosition()).img);
                dialog.show();
                AndroidNetworking.initialize(context);
                AndroidNetworking
                        .get("https://yahoo-finance15.p.rapidapi.com/api/v1/markets/options/most-active?type=STOCKS")
                        .addHeaders("x-rapidapi-key", "7b954fcb12msh229db1f90919780p10c655jsn5e40f3bcd1d7")
                        .addHeaders("x-rapidapi-host", "yahoo-finance15.p.rapidapi.com")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressBar.setVisibility(View.GONE);
                                Log.d("response",response.toString());
                                try {
                                    JSONArray arr=response.getJSONArray("body");

                                        JSONObject stock=arr.getJSONObject(stocks.get(holder.getAdapterPosition()).index);
                                        details.add(stock.getString("symbolName"));
                                        details.add(stock.getString("lastPrice"));
                                        details.add(stock.getString("percentChange"));
                                    Name.setText("Stock Name : "+details.get(0));
                                    LastPrice.setText("LastPrice : "+details.get(1));
                                    PercentChange.setText("PercentChange : "+details.get(2));
//                                    dialog.show();

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults=new FilterResults();
                if(constraint==null||constraint.length()==0)
                {
                    filterResults.count=stocksFilterable.size();
                    filterResults.values=stocksFilterable;
                }
                else {
                    String charVal=constraint.toString().toLowerCase();
                    ArrayList<Stock> arrayList=new ArrayList<>();
                    for (Stock stock:stocksFilterable) {
                        if(stock.Des.toLowerCase().contains(charVal))
                        {
                            arrayList.add(stock);
                        }
                        filterResults.count=arrayList.size();
                        filterResults.values=arrayList;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                stocks= (ArrayList<Stock>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView stockDes;
//        ImageView stockImg;
        RelativeLayout stockCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stockDes=itemView.findViewById(R.id.stockDes);
//            stockImg=itemView.findViewById(R.id.stockImg);
            stockCard=itemView.findViewById(R.id.stockCard);
        }
    }
}
