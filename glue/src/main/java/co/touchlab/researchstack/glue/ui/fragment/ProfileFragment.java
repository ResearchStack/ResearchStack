package co.touchlab.researchstack.glue.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.User;
import co.touchlab.researchstack.glue.ui.views.ProfileItemView;

/**
 * Created by bradleymcdermott on 10/28/15.
 */
public class ProfileFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);

        User user = ResearchStack.getInstance()
                                 .getCurrentUser();

        // TODO header should be a custom view?
        AppCompatTextView name = (AppCompatTextView) view.findViewById(R.id.name);
        name.setText(user.getName());

        AppCompatTextView email = (AppCompatTextView) view.findViewById(R.id.email);
        email.setText(user.getEmail());

        ImageView profileImage = (ImageView) view.findViewById(R.id.profile_image);
        RxView.clicks(profileImage)
              .subscribe(clickedView -> showUnimplementedToast());

        User.UserInfoType[] userInfoTypes = ResearchStack.getInstance()
                                                         .getUserInfoTypes();

        for(User.UserInfoType userInfoType : userInfoTypes)
        {
            View.OnClickListener clickListener = null;
            int labelId;
            String value;
            switch(userInfoType)
            {
                case DateOfBirth:
                    Calendar birthdate = new GregorianCalendar();
                    birthdate.setTime(user.getBirthDate());
                    DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM,
                                                                   Locale.getDefault());

                    labelId = R.string.birthdate;
                    value = format.format(birthdate.getTime());

                    clickListener = v -> new DatePickerDialog(getActivity(),
                                                              (datePickerView, year, monthOfYear, dayOfMonth) -> {
                                                                  birthdate.set(year, monthOfYear,
                                                                                dayOfMonth);
                                                                  user.setBirthDate(
                                                                          birthdate.getTime());
                                                                  ((ProfileItemView) v).setValue(
                                                                          format.format(
                                                                                  birthdate.getTime()));
                                                              }, birthdate.get(Calendar.YEAR),
                                                              birthdate.get(Calendar.MONTH),
                                                              birthdate.get(
                                                                      Calendar.DAY_OF_MONTH)).show();
                    break;
                case Weight:
                    labelId = R.string.weight;
                    value = String.valueOf(user.getWeight());
                    break;
                case Height:
                    labelId = R.string.height;
                    value = formatHeightString(user.getHeight());
                    break;
                case BiologicalSex:
                    labelId = R.string.biological_sex;
                    value = user.getBiologicalSex();
                    break;
                default:
                    continue;
            }
            ProfileItemView itemView = (ProfileItemView) inflater.inflate(
                    R.layout.view_profile_item, linearLayout, false);
            itemView.setLabel(labelId);
            itemView.setValue(value);
            itemView.setOnClickListener(clickListener);
            linearLayout.addView(itemView);
        }
        return view;
    }

    private void showUnimplementedToast()
    {
        Toast.makeText(getActivity(), "TODO: show appropriate dialog", Toast.LENGTH_SHORT)
             .show();
    }

    private String formatHeightString(int height)
    {
        int feet = height / 12;
        int inches = height % 12;
        return String.format("%d\' %d\"", feet, inches);
    }
}
