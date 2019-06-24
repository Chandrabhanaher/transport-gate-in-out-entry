package com.example.ugc.ugc.reports;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.ugc.ugc.R;

import java.util.ArrayList;
import java.util.List;

public class Cust_Adapter extends RecyclerView.Adapter<Cust_Adapter.ViewHolder> implements Filterable {
    Context context;
    List<CustList> custList;
    List<CustList> custLists;
    public Cust_Adapter(Context context, List<CustList> custList) {
        super();
        this.context = context;
        this.custList = custList;
        this.custLists = custList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_items,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustList cl = custLists.get(position);
        holder.txtVehicalNo.setText(cl.getVEHICLENO());
        holder.txtSupp.setText(cl.getSUPPLIERNAME());

        String indate = cl.getINTIME();
        String outDate = cl.getOUTTIME();
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
        return custLists.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()){
                    custLists = custList;
                }else{
                    List<CustList> custL = new ArrayList<>();
                    for(CustList row:custList){
                        if(row.getVEHICLENO().toLowerCase().contains(charString.toLowerCase())|| row.getSUPPLIERNAME().toLowerCase().contains(charString.toLowerCase()) || row.getINTIME().toLowerCase().contains(charString.toLowerCase()) || row.getOUTTIME().toLowerCase().contains(charString.toLowerCase())){
                            custL.add(row);
                        }
                    }
                    custLists = custL;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = custLists;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                custLists = (ArrayList<CustList>) results.values;
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
