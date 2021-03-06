package de.simonmayrshofer.simonsblog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.simonmayrshofer.simonsblog.pojos.Article;

public class ArticlesListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<Article> articles;

    public ArticlesListAdapter(Context context, List<Article> articles) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.articles = articles;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Article getItem(int i) {
        return articles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = inflater.inflate(R.layout.list_item_article, viewGroup, false);

        TextView dateView = (TextView) itemView.findViewById(R.id.list_item_article_date);
        dateView.setText(Helpers.convertDate(articles.get(i).createdAt));
        TextView titleView = (TextView) itemView.findViewById(R.id.list_item_article_title);
        titleView.setText(articles.get(i).title);
        TextView bodyView = (TextView) itemView.findViewById(R.id.list_item_article_body);
        bodyView.setText(articles.get(i).text);
        TextView commentsView = (TextView) itemView.findViewById(R.id.list_item_article_comments);
        commentsView.setText(Helpers.getCommentCountString(articles.get(i).comments().size()));

        if (i + 1 == getCount()) {
            itemView.findViewById(R.id.list_item_bottom_space).setVisibility(View.VISIBLE);
        }

        return itemView;
    }
}
