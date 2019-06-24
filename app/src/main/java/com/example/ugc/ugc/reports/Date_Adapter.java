package com.example.ugc.ugc.reports;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ugc.ugc.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Date_Adapter extends RecyclerView.Adapter<Date_Adapter.ViewHolder> implements Filterable {
    Context context;
    ArrayList<Date_Report> disNewsList;
    ArrayList<Date_Report> disNewsLists;
    public Date_Adapter(Context context, ArrayList<Date_Report> disNewsList) {
        this.context = context;
        this.disNewsList = disNewsList;
        this.disNewsLists = disNewsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_items,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Date_Report dr = disNewsLists.get(position);
        holder.txtVehicalNo.setText(dr.getVEHICLENO());
        holder.txtSupp.setText(dr.getSUPPLIERNAME());

        String indate = dr.getINTIME();
        String outDate = dr.getOUTTIME();
        holder.txtIn.setText(indate);
        String tt = "null";
        if(tt.equals(outDate)){
            holder.txtOut.setText(" ");
        }else {
            holder.txtOut.setText(outDate);
        }

    }

    @Override
    public int getItemCount() {
        return disNewsLists.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()){
                    disNewsLists = disNewsList;
                }else{
                    ArrayList<Date_Report> custL = new ArrayList<>();
                    for(Date_Report row:disNewsList){
                        if(row.getVEHICLENO().toLowerCase().contains(charString.toLowerCase())|| row.getSUPPLIERNAME().toLowerCase().contains(charString.toLowerCase()) || row.getINTIME().toLowerCase().contains(charString.toLowerCase()) || row.getOUTTIME().toLowerCase().contains(charString.toLowerCase())){
                            custL.add(row);
                        }
                    }
                    disNewsLists= custL;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = disNewsLists;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                disNewsLists = (ArrayList<Date_Report>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVehicalNo, txtSupp, txtIn, txtOut;
        public ViewHolder(View itemView) {
            super(itemView);
            txtVehicalNo = (TextView)itemView.findViewById(R.id.v_no);
            txtSupp = (TextView)itemView.findViewById(R.id.spname);
            txtIn = (TextView)itemView.findViewById(R.id.Indate);
            txtOut = (TextView)itemView.findViewById(R.id.out);
        }
    }
}
