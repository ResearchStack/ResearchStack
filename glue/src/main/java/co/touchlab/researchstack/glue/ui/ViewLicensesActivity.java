package co.touchlab.researchstack.glue.ui;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import java.io.InputStream;

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.StudyOverviewModel;
import co.touchlab.researchstack.glue.utils.JsonUtils;

public class ViewLicensesActivity extends AppCompatActivity
{

    public static Intent newIntent(Context context)
    {
        return  new Intent(context, ViewLicensesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        int padding = getResources().getDimensionPixelSize(R.dimen.padding_medium);
        //TODO Find a better way of presenting the data. Right now, the TV only scrolls manually. No fling.
        //TODO Probably best to just use a recycler view for this.
        TextView tv = new TextView(this);
        tv.setVerticalScrollBarEnabled(true);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setPadding(padding,padding,padding,padding);

        StudyOverviewModel model = parseSectionModel();

        for(int i = 0; i < model.getQuestions().size(); i++)
        {
            StudyOverviewModel.Question question = model.getQuestions().get(i);

            try {
                Resources res = getResources();

                //Read in the license
                int imageResId = res.getIdentifier(question.getDetails(), "raw", getPackageName());
                InputStream in_s = res.openRawResource(imageResId);
                byte[] b = new byte[in_s.available()];
                in_s.read(b);

                //Add some space between licenses
                if (i > 0)
                {
                    tv.append("\n\n\n\n");
                }

                //Create title w/ bold typeface
                final SpannableString boldSpan = new SpannableString(question.getTitle());
                boldSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, boldSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                boldSpan.setSpan(new RelativeSizeSpan(1.5f), 0, boldSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.append(boldSpan);

                //Append the license text
                tv.append("\n");
                tv.append(new String(b));

                in_s.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        super.setContentView(tv);
    }

    //TODO Read on main thread for intense UI blockage.
    private StudyOverviewModel parseSectionModel()
    {
        int fileResId = ResearchStack.getInstance().getLicenseSections();
        return JsonUtils.loadClassFromRawJson(this, StudyOverviewModel.class, fileResId);
    }
}
