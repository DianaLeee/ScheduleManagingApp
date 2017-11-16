package final_project.mobile.lecture.ma02_20141095;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract.PhoneLookup;


import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

/**
 * Created by LeeDaYeon on 2017-03-23.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;
    List<Appoint> items;
    int item_layout;

    private MyDBHelper myDBHelper;


    public RecyclerAdapter(Context context, List<Appoint> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_appoint, parent, false);
        //v.setOnClickListener(mOnClickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Appoint item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getContent());
        holder.tvPlace_name.setText(item.getPlace_name() + "");
        holder.tvsName.setText("with " + item.getsName() + "");
        holder.tvTime.setText(formatTimeString(item.getTime()));
        Drawable d = new BitmapDrawable(getFacebookPhoto(item.getsId()));
        holder.imageView.setImageDrawable(d);

//        cardView
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("appoint", items.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });



//        longClickListener- delete
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Remove")
                        .setMessage("Do you want to remove this schedule?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton){
                                myDBHelper = new MyDBHelper(context);
                                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                                db.execSQL("DELETE FROM " + myDBHelper.TABLE_NAME + " WHERE _id = " + items.get(position).get_id() + ";");
                                items.remove(position);
                                myDBHelper.close();

                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void updateItemList(List<Appoint> listItems) {
        this.items = listItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;
        TextView tvPlace_name;
        TextView tvsName;
        TextView tvTime;
        ImageView imageView;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvPlace_name = (TextView) itemView.findViewById(R.id.tvPlace_name);
            tvsName = (TextView) itemView.findViewById(R.id.tvsName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            cardView = (CardView)itemView.findViewById(R.id.cardview);
        }

    }

//    load picture from contact
    public Bitmap getFacebookPhoto (long userId) {
        ContentResolver cr = context.getContentResolver();
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);
        if (photoUri != null) {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri);
            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        } else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(context.getResources(), R.mipmap.human);
            return defaultPhoto;
        }
        Bitmap defaultPhoto = BitmapFactory.decodeResource(context.getResources(), R.mipmap.human);
        return defaultPhoto;
    }


    public static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String formatTimeString(long tempDate) {
        Calendar cal_today = Calendar.getInstance();
        long curTime = cal_today.getTimeInMillis();
        long diffTime = (tempDate-curTime) / 1000;

        String msg = null;
        Log.e("MAinActivity : ", Long.toString(diffTime));
        if (diffTime < 0) {
            msg = "It's over";
        }
        else if (diffTime < TIME_MAXIMUM.SEC) {
            // sec
            msg = "Time soon";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            // min
            msg = diffTime + "minutes later";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            // hour
            msg = (diffTime) + "hours later";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            // day
            msg = (diffTime) + "days after";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            // day
            msg = (diffTime) + "months after";
        } else {
            msg = "over 1 year";
        }

        return msg;
    }
}