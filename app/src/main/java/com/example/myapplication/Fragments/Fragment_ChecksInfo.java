package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import android.widget.TextView;

public class Fragment_ChecksInfo extends Fragment {

    private static final String ARG_CHILD_ID = "childID";
    private static final String ARG_CHECK_TYPE = "checkType";

    private int childID;
    private String checkType;

    private TextView infoTextView;
    private Button nextButton;

    public Fragment_ChecksInfo() {
        // Required empty public constructor
    }

    public static Fragment_ChecksInfo newInstance(int childID, String checkType) {
        Fragment_ChecksInfo fragment = new Fragment_ChecksInfo();
        Bundle args = new Bundle();
        args.putInt(ARG_CHILD_ID, childID);
        args.putString(ARG_CHECK_TYPE, checkType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checksinfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        infoTextView = view.findViewById(R.id.info_text_view);
        nextButton = view.findViewById(R.id.next_button);

        if (getArguments() != null) {
            childID = getArguments().getInt(ARG_CHILD_ID, 0);
            checkType = getArguments().getString(ARG_CHECK_TYPE, "").trim();
        }

        // Load information based on checkType
        String infoContent = getInfoContentForCheckType(checkType);
        infoTextView.setText(Html.fromHtml(infoContent));

        nextButton.setOnClickListener(v -> {
            // Proceed to ParentQuestionsFragment
            ParentQuestionsFragment parentQuestionsFragment = ParentQuestionsFragment.newInstance(childID, checkType);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_encapsulating, parentQuestionsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private String getInfoContentForCheckType(String checkType) {
        switch (checkType.toLowerCase()) {
            case "1-4 weeks":
                return "<section>" +
                        "<h2>The 1 to 4 Week Visit</h2>" +
                        "<p>Your first visit with a child and family health nurse usually takes place in the family home. This is a good time for the parent(s) and the nurse to get to know each other and talk about any concerns.</p>" +
                        "<h3>Topics for Discussion May Include:</h3>" +
                        "<ul>" +
                        "<li><strong>Health and Safety:</strong> Feeding your baby, safe sleeping and Sudden Unexpected Death in Infancy (SUDI), immunisations, safety, growth.</li>" +
                        "<li><strong>Development:</strong> Crying, comforting your baby, talking to your baby – communication, language, and play.</li>" +
                        "<li><strong>Family:</strong> Using the 'Personal Health Record' (Blue Book), the role of health professionals, parents' emotional health, mother's general health (diet, rest, breast care, exercise, oral health), parent groups and support networks, smoking and/or vaping, work/childcare.</li>" +
                        "</ul>" +
                        "<div style='border:1px solid #ccc; padding:10px; background-color:#f9f9f9;'>" +
                        "<p><strong>Still Smoking and/or Vaping?</strong><br>" +
                        "Smoking increases your baby's risk of Sudden Infant Death Syndrome (SIDS). Call Quitline at 13 QUIT (13 7848) or visit <a href='https://www.icanquit.com.au'>www.icanquit.com.au</a> for support.</p>" +
                        "</div>" +
                        "</section>";

            case "6-8 weeks":
                return "<section>" +
                        "<h2>The 6 to 8 Week Visit</h2>" +
                        "<p>During this visit, you can discuss any issues or questions that have arisen since your last appointment.</p>" +
                        "<h3>Topics for Discussion May Include:</h3>" +
                        "<ul>" +
                        "<li>My development (Learn the Signs. Act Early)</li>" +
                        "<li>Additional parent/carer questions</li>" +
                        "<li>Child health check</li>" +
                        "</ul>" +
                        "<h3>Health and Safety</h3>" +
                        "<ul>" +
                        "<li>Feeding your baby (including breastfeeding)</li>" +
                        "<li>Vaccinations – the first scheduled vaccinations start at 6 weeks of age to protect against whooping cough (pertussis) and pneumococcal</li>" +
                        "<li>Safe sleeping and Sudden Unexpected Death in Infancy (SUDI)</li>" +
                        "<li>How to be sun smart</li>" +
                        "<li>Growth</li>" +
                        "</ul>" +
                        "<h3>Development</h3>" +
                        "<ul>" +
                        "<li>Crying</li>" +
                        "<li>Comforting your baby</li>" +
                        "<li>Talking to your baby – communication, language, and play</li>" +
                        "</ul>" +
                        "<h3>Family</h3>" +
                        "<ul>" +
                        "<li>Parent groups</li>" +
                        "<li>Mother's health (diet, rest, family planning, exercise)</li>" +
                        "<li>Parents' emotional health</li>" +
                        "<li>Smoking and/or vaping</li>" +
                        "<li>Positive parenting and developing a close relationship with your baby</li>" +
                        "</ul>" +
                        "<div style='border:1px solid #ccc; padding:10px; background-color:#f9f9f9;'>" +
                        "<p><strong>Still Smoking and/or Vaping?</strong><br>" +
                        "Smoking increases your baby's risk of SIDS. Call Quitline at 13 QUIT (13 7848) or visit <a href='https://www.icanquit.com.au'>www.icanquit.com.au</a> for support.</p>" +
                        "</div>" +
                        "</section>";

            case "4 month":
                return "<section>" +
                        "<h2>4 Month Immunisations</h2>" +
                        "<p>The NSW Immunisation Schedule recommends that children are immunised at the following ages:</p>" +
                        "<ul>" +
                        "<li>Birth</li>" +
                        "<li>6 weeks</li>" +
                        "<li>4 months</li>" +
                        "<li>6 months</li>" +
                        "<li>12 months</li>" +
                        "<li>18 months</li>" +
                        "<li>4 years</li>" +
                        "</ul>" +
                        "<p>The vaccines given at 4 months of age are a repeat of the vaccines given at 6 weeks to provide early and strong protection against serious diseases, including whooping cough and pneumococcal infections. For more details, view the NSW Immunisation Schedule at <a href='https://health.nsw.gov.au/schedule'>health.nsw.gov.au/schedule</a>. More information is available at <a href='https://health.nsw.gov.au/vaccinate'>health.nsw.gov.au/vaccinate</a>.</p>" +
                        "<div style='border:1px solid #ffcc00; padding:10px; background-color:#fff8e1;'>" +
                        "<p><strong>Note:</strong> Even though there's no scheduled check at 4 months, consult your doctor or child and family health nurse if you have any concerns.</p>" +
                        "</div>" +
                        "<h3>My Development – Learn the Signs. Act Early.</h3>" +
                        "<h4>Social/Emotional Milestones</h4>" +
                        "<ul>" +
                        "<li>Smiles independently to gain your attention</li>" +
                        "<li>Giggles when you try to make them laugh</li>" +
                        "<li>Seeks your attention through sounds and movements</li>" +
                        "</ul>" +
                        "<h4>Language/Communication Milestones</h4>" +
                        "<ul>" +
                        "<li>Makes cooing sounds like 'oooo' or 'aahh'</li>" +
                        "<li>Responds to your voice by making sounds</li>" +
                        "<li>Turns head towards your voice</li>" +
                        "</ul>" +
                        "<h4>Cognitive Milestones</h4>" +
                        "<ul>" +
                        "<li>Opens mouth at the sight of breast or bottle when hungry</li>" +
                        "<li>Shows interest in their own hands</li>" +
                        "</ul>" +
                        "<h4>Movement/Physical Development Milestones</h4>" +
                        "<ul>" +
                        "<li>Holds head steady without support when held</li>" +
                        "<li>Grasps a toy placed in their hand</li>" +
                        "<li>Reaches out to swat at toys</li>" +
                        "<li>Brings hands to mouth</li>" +
                        "<li>Pushes up onto elbows or forearms when on tummy</li>" +
                        "</ul>" +
                        "</section>";

            case "6 month":
                return "<section>" +
                        "<h2>The 6 Month Visit</h2>" +
                        "<p>This visit allows you to address any concerns and discuss your baby's development.</p>" +
                        "<h3>Topics for Discussion May Include:</h3>" +
                        "<ul>" +
                        "<li>My development (Learn the Signs. Act Early)</li>" +
                        "<li>Additional parent/carer questions</li>" +
                        "<li>Child health check</li>" +
                        "</ul>" +
                        "<h3>Health and Safety</h3>" +
                        "<ul>" +
                        "<li>Sleep patterns and safe sleeping practices</li>" +
                        "<li>Introducing healthy eating habits</li>" +
                        "<li>Oral hygiene and teething care</li>" +
                        "<li>Immunisation updates</li>" +
                        "<li>Sun safety</li>" +
                        "<li>General safety precautions</li>" +
                        "<li>Monitoring growth milestones</li>" +
                        "</ul>" +
                        "<h3>Family</h3>" +
                        "<ul>" +
                        "<li>Managing sibling relationships and rivalry</li>" +
                        "<li>Engaging in play activities</li>" +
                        "<li>Parental emotional health</li>" +
                        "<li>Participating in playgroups</li>" +
                        "<li>Smoking and/or vaping cessation support</li>" +
                        "<li>Fostering a close relationship with your baby through positive parenting</li>" +
                        "</ul>" +
                        "<div style='border:1px solid #ccc; padding:10px; background-color:#f9f9f9;'>" +
                        "<p><strong>Still Smoking and/or Vaping?</strong><br>" +
                        "Smoking increases your baby's risk of SIDS. Call Quitline at 13 QUIT (13 7848) or visit <a href='https://www.icanquit.com.au'>www.icanquit.com.au</a> for support.</p>" +
                        "</div>" +
                        "<h3>My Development – Learn the Signs. Act Early.</h3>" +
                        "<h4>Social/Emotional Milestones</h4>" +
                        "<ul>" +
                        "<li>Recognizes familiar people</li>" +
                        "<li>Enjoys looking at themselves in a mirror</li>" +
                        "<li>Laughs spontaneously</li>" +
                        "</ul>" +
                        "<h4>Language/Communication Milestones</h4>" +
                        "<ul>" +
                        "<li>Engages in back-and-forth sounds with you</li>" +
                        "<li>Blows raspberries and makes squealing noises</li>" +
                        "</ul>" +
                        "<h4>Cognitive Milestones</h4>" +
                        "<ul>" +
                        "<li>Explores objects by putting them in their mouth</li>" +
                        "<li>Reaches for toys they are interested in</li>" +
                        "<li>Closes lips to indicate they are full</li>" +
                        "</ul>" +
                        "<h4>Movement/Physical Development Milestones</h4>" +
                        "<ul>" +
                        "<li>Rolls from tummy to back</li>" +
                        "<li>Pushes up with straight arms during tummy time</li>" +
                        "<li>Sits with support, leaning on hands</li>" +
                        "</ul>" +
                        "<h4>Additional Questions to Consider</h4>" +
                        "<ul>" +
                        "<li>What activities do you enjoy with your baby?</li>" +
                        "<li>What does your baby like to do?</li>" +
                        "<li>Are there any behaviors that concern you?</li>" +
                        "<li>Has your baby lost any previously acquired skills?</li>" +
                        "<li>Does your baby have any special healthcare needs or were they born prematurely?</li>" +
                        "</ul>" +
                        "<p>At around 6 months, it's recommended to start introducing solid foods while continuing to breastfeed until 12 months or longer. For more information on starting family foods, refer to page 20 of your guide.</p>" +
                        "<h3>Your Child's Teeth – Keeping Them Healthy</h3>" +
                        "<p>Oral health is crucial for overall well-being and speech development. Early detection of dental issues can prevent serious problems.</p>" +
                        "<h4>Teething Timeline</h4>" +
                        "<table border='1' cellpadding='5' cellspacing='0'>" +
                        "<tr><th>Order</th><th>Tooth</th><th>Age of Eruption</th></tr>" +
                        "<tr><td>1-4</td><td>Incisors</td><td>6-12 months</td></tr>" +
                        "<tr><td>5-6</td><td>First Molars</td><td>12-20 months</td></tr>" +
                        "<tr><td>7-8</td><td>Canines</td><td>18-24 months</td></tr>" +
                        "<tr><td>9-10</td><td>Second Molars</td><td>24-30 months</td></tr>" +
                        "</table>" +
                        "<h4>Bottles and Dummies</h4>" +
                        "<ul>" +
                        "<li>Breast milk is best, but if using bottles, ensure proper hygiene to prevent tooth decay.</li>" +
                        "</ul>" +
                        "<h4>Toothbrushing Tips</h4>" +
                        "<ul>" +
                        "<li>Maintain your own oral hygiene to prevent transferring bacteria.</li>" +
                        "<li>Start cleaning your child's teeth as soon as they appear with a soft toothbrush.</li>" +
                        "<li>From 18 months, use a pea-sized amount of low-fluoride toothpaste twice daily.</li>" +
                        "<li>Schedule an oral health check-up by their first birthday.</li>" +
                        "</ul>" +
                        "</section>";

            case "12 month":
                return "<section>" +
                        "<h2>The 12 Month Visit</h2>" +
                        "<p>This visit focuses on your child's growth and development over the past year.</p>" +
                        "<h3>Topics for Discussion May Include:</h3>" +
                        "<ul>" +
                        "<li>My child's development (Learn the Signs. Act Early)</li>" +
                        "<li>Additional parent/carer questions</li>" +
                        "<li>Child health check</li>" +
                        "</ul>" +
                        "<h3>Health and Safety</h3>" +
                        "<ul>" +
                        "<li>Healthy eating and encouraging active play</li>" +
                        "<li>Dental care for your child</li>" +
                        "<li>Sleep patterns</li>" +
                        "<li>Immunisations</li>" +
                        "<li>Safety precautions</li>" +
                        "<li>Sun safety</li>" +
                        "<li>Growth monitoring</li>" +
                        "</ul>" +
                        "<h3>Family</h3>" +
                        "<ul>" +
                        "<li>Managing sibling relationships and rivalry</li>" +
                        "<li>Positive parenting techniques</li>" +
                        "<li>Parental emotional health</li>" +
                        "<li>Smoking and/or vaping cessation support</li>" +
                        "<li>Participation in playgroups or selecting early childhood education services</li>" +
                        "</ul>" +
                        "<div style='border:1px solid #ccc; padding:10px; background-color:#f9f9f9;'>" +
                        "<p><strong>Still Smoking and/or Vaping?</strong><br>" +
                        "Smoking increases your baby's risk of SIDS. Call Quitline at 13 QUIT (13 7848) or visit <a href='http://www.icanquit.com.au'>www.icanquit.com.au</a> for support.</p>" +
                        "</div>" +
                        "<h3>My Development – Learn the Signs. Act Early.</h3>" +
                        "<h4>Social/Emotional Milestones</h4>" +
                        "<ul>" +
                        "<li>Engages in games like pat-a-cake</li>" +
                        "</ul>" +
                        "<h4>Language/Communication Milestones</h4>" +
                        "<ul>" +
                        "<li>Waves 'bye-bye'</li>" +
                        "<li>Calls a parent 'mama' or 'dada' or another special name</li>" +
                        "<li>Understands 'no' and pauses or stops when you say it</li>" +
                        "</ul>" +
                        "<h4>Cognitive Milestones</h4>" +
                        "<ul>" +
                        "<li>Puts objects in a container, like a block in a cup</li>" +
                        "<li>Looks for things they see you hide</li>" +
                        "</ul>" +
                        "<h4>Movement/Physical Development Milestones</h4>" +
                        "<ul>" +
                        "<li>Pulls up to stand</li>" +
                        "<li>Walks holding onto furniture</li>" +
                        "<li>Drinks from a cup without a lid when you hold it</li>" +
                        "<li>Picks things up between thumb and index finger</li>" +
                        "</ul>" +
                        "<h4>Additional Questions to Consider</h4>" +
                        "<ul>" +
                        "<li>Activities you and your child enjoy together</li>" +
                        "<li>Your child's favorite activities</li>" +
                        "<li>Any concerns about your child's behavior</li>" +
                        "<li>Any loss of previously acquired skills</li>" +
                        "<li>Special healthcare needs or premature birth</li>" +
                        "</ul>" +
                        "<h3>Thinking About Early Childhood Education and Care?</h3>" +
                        "<p>Choosing an early childhood education and care service is an important decision. These services provide opportunities for socialization, independence, and learning.</p>" +
                        "<p>More information can be found on the Department of Education website at <a href='https://education.nsw.gov.au/early-education'>education.nsw.gov.au/early-education</a>.</p>" +
                        "</section>";

            case "18 month":
                return "<section>" +
                        "<h2>The 18 Month Visit</h2>" +
                        "<p>This visit focuses on your toddler's rapid development and any concerns you may have.</p>" +
                        "<h3>Topics for Discussion May Include:</h3>" +
                        "<ul>" +
                        "<li>My development (Learn the Signs. Act Early)</li>" +
                        "<li>Additional parent/carer questions</li>" +
                        "<li>Child health check</li>" +
                        "</ul>" +
                        "<h3>Health and Safety</h3>" +
                        "<ul>" +
                        "<li>Healthy family eating habits</li>" +
                        "<li>Sleep routines</li>" +
                        "<li>Dental care</li>" +
                        "<li>Sun safety</li>" +
                        "<li>Growth tracking</li>" +
                        "<li>Immunisations</li>" +
                        "</ul>" +
                        "<h3>Development</h3>" +
                        "<ul>" +
                        "<li>Behavioral observations</li>" +
                        "<li>Starting toilet training</li>" +
                        "<li>Encouraging active play</li>" +
                        "</ul>" +
                        "<h3>Family</h3>" +
                        "<ul>" +
                        "<li>Sibling interactions</li>" +
                        "<li>Positive parenting strategies</li>" +
                        "<li>Participation in playgroups or childcare</li>" +
                        "<li>Smoking and/or vaping cessation support</li>" +
                        "</ul>" +
                        "<div style='border:1px solid #ccc; padding:10px; background-color:#f9f9f9;'>" +
                        "<p><strong>Still Smoking and/or Vaping?</strong><br>" +
                        "Smoking increases your child's risk of SIDS. Call Quitline at 13 QUIT (13 7848) or visit <a href='http://www.icanquit.com.au'>www.icanquit.com.au</a> for support.</p>" +
                        "</div>" +
                        "<h3>My Development – Learn the Signs. Act Early.</h3>" +
                        "<h4>Social/Emotional Milestones</h4>" +
                        "<ul>" +
                        "<li>Moves away from you but checks to ensure you're nearby</li>" +
                        "<li>Points to show you something interesting</li>" +
                        "<li>Reaches out hands for washing</li>" +
                        "<li>Looks at book pages with you</li>" +
                        "<li>Helps with dressing by pushing arm through sleeve or lifting foot</li>" +
                        "</ul>" +
                        "<h4>Language/Communication Milestones</h4>" +
                        "<ul>" +
                        "<li>Tries to say three or more words besides 'mama' or 'dada'</li>" +
                        "<li>Follows one-step directions without gestures</li>" +
                        "</ul>" +
                        "<h4>Cognitive Milestones</h4>" +
                        "<ul>" +
                        "<li>Imitates household chores like sweeping</li>" +
                        "<li>Engages with toys in simple ways, like pushing a toy car</li>" +
                        "</ul>" +
                        "<h4>Movement/Physical Development Milestones</h4>" +
                        "<ul>" +
                        "<li>Walks independently</li>" +
                        "<li>Scribbles spontaneously</li>" +
                        "<li>Drinks from a cup without a lid, may spill sometimes</li>" +
                        "<li>Feeds self with fingers</li>" +
                        "<li>Attempts to use a spoon</li>" +
                        "<li>Climbs onto and off furniture without help</li>" +
                        "</ul>" +
                        "<h4>Additional Questions to Consider</h4>" +
                        "<ul>" +
                        "<li>Activities you and your child enjoy together</li>" +
                        "<li>Your child's favorite activities</li>" +
                        "<li>Any concerns about your child's behavior</li>" +
                        "<li>Any loss of previously acquired skills</li>" +
                        "<li>Special healthcare needs or premature birth</li>" +
                        "</ul>" +
                        "<p>Discuss any concerns with your Child and Family Health Nurse or GP and consider developmental screening.</p>" +
                        "</section>";

            case "2 year":
                return "<section>" +
                        "<h2>The 2 Year Visit</h2>" +
                        "<p>This visit focuses on your child's development as they approach early childhood.</p>" +
                        "<h3>Topics for Discussion May Include:</h3>" +
                        "<ul>" +
                        "<li>My development (Learn the Signs. Act Early)</li>" +
                        "<li>Additional parent/carer questions</li>" +
                        "<li>Child health check</li>" +
                        "</ul>" +
                        "<h3>Health and Safety</h3>" +
                        "<ul>" +
                        "<li>Healthy family eating and active play</li>" +
                        "<li>Immunisations</li>" +
                        "<li>Dental care</li>" +
                        "<li>Sun safety</li>" +
                        "<li>Sleep patterns</li>" +
                        "<li>Growth monitoring</li>" +
                        "</ul>" +
                        "<h3>Development</h3>" +
                        "<ul>" +
                        "<li>Mobility changes</li>" +
                        "<li>Behavioral observations</li>" +
                        "<li>Toilet training progress</li>" +
                        "<li>Communication and social skills</li>" +
                        "<li>Reading to build literacy skills</li>" +
                        "</ul>" +
                        "<h3>Family</h3>" +
                        "<ul>" +
                        "<li>Sibling relationships</li>" +
                        "<li>Parenting practices for managing feelings and behavior</li>" +
                        "<li>Participation in childcare or playgroups</li>" +
                        "<li>Smoking and/or vaping cessation support</li>" +
                        "</ul>" +
                        "<div style='border:1px solid #ccc; padding:10px; background-color:#f9f9f9;'>" +
                        "<p><strong>Still Smoking and/or Vaping?</strong><br>" +
                        "Smoking increases your child's risk of SIDS. Call Quitline at 13 QUIT (13 7848) or visit <a href='http://www.icanquit.com.au'>www.icanquit.com.au</a> for support.</p>" +
                        "</div>" +
                        "<h3>My Development – Learn the Signs. Act Early.</h3>" +
                        "<h4>Social/Emotional Milestones</h4>" +
                        "<ul>" +
                        "<li>Notices when others are hurt or upset</li>" +
                        "<li>Looks at your face to gauge reactions in new situations</li>" +
                        "</ul>" +
                        "<h4>Language/Communication Milestones</h4>" +
                        "<ul>" +
                        "<li>Points to items in a book when asked</li>" +
                        "<li>Uses at least two words together</li>" +
                        "<li>Points to body parts when prompted</li>" +
                        "<li>Uses gestures like blowing a kiss or nodding yes</li>" +
                        "</ul>" +
                        "<h4>Cognitive Milestones</h4>" +
                        "<ul>" +
                        "<li>Holds an item in one hand while using the other</li>" +
                        "<li>Attempts to use switches, knobs, or buttons on toys</li>" +
                        "<li>Plays with multiple toys simultaneously</li>" +
                        "</ul>" +
                        "<h4>Movement/Physical Development Milestones</h4>" +
                        "<ul>" +
                        "<li>Kicks a ball</li>" +
                        "<li>Runs</li>" +
                        "<li>Walks up a few stairs with or without help</li>" +
                        "<li>Eats with a spoon</li>" +
                        "</ul>" +
                        "<h4>Additional Questions to Consider</h4>" +
                        "<ul>" +
                        "<li>Activities you and your child enjoy together</li>" +
                        "<li>Your child's favorite activities</li>" +
                        "<li>Any concerns about your child's behavior</li>" +
                        "<li>Any loss of previously acquired skills</li>" +
                        "<li>Special healthcare needs or premature birth</li>" +
                        "</ul>" +
                        "<h3>Enrolling Your Child in Early Childhood Education</h3>" +
                        "<p>As your child turns two, consider enrolling them in early childhood education. Access to quality preschool programs leads to improved outcomes.</p>" +
                        "<p>More information is available at <a href='https://education.nsw.gov.au/early-childhood-education/information-for-parents-and-carers'>education.nsw.gov.au/early-childhood-education/information-for-parents-and-carers</a>.</p>" +
                        "</section>";

            case "3 year":
                return "<section>" +
                        "<h2>The 3 Year Visit</h2>" +
                        "<p>This visit focuses on supporting your child's growing independence and preparing for preschool.</p>" +
                        "<h3>Topics for Discussion May Include:</h3>" +
                        "<ul>" +
                        "<li>My development (Learn the Signs. Act Early)</li>" +
                        "<li>Additional parent/carer questions</li>" +
                        "<li>Child health check</li>" +
                        "</ul>" +
                        "<h3>Health and Safety</h3>" +
                        "<ul>" +
                        "<li>Healthy family eating</li>" +
                        "<li>Immunisations</li>" +
                        "<li>Dental care</li>" +
                        "<li>Sun safety</li>" +
                        "<li>Growth monitoring</li>" +
                        "</ul>" +
                        "<h3>Development</h3>" +
                        "<ul>" +
                        "<li>Managing independent behavior</li>" +
                        "<li>Toilet training progress</li>" +
                        "<li>Reading to build literacy skills</li>" +
                        "<li>Encouraging active play</li>" +
                        "</ul>" +
                        "<h3>Family</h3>" +
                        "<ul>" +
                        "<li>Sibling relationships</li>" +
                        "<li>Parenting practices</li>" +
                        "<li>Participation in childcare or preschool</li>" +
                        "<li>Smoking and/or vaping cessation support</li>" +
                        "</ul>" +
                        "<div style='border:1px solid #ccc; padding:10px; background-color:#f9f9f9;'>" +
                        "<p><strong>Still Smoking and/or Vaping?</strong><br>" +
                        "Smoking increases your child's risk of SIDS. Call Quitline at 13 QUIT (13 7848) or visit <a href='http://www.icanquit.com.au'>www.icanquit.com.au</a> for support.</p>" +
                        "</div>" +
                        "<h3>My Development – Learn the Signs. Act Early.</h3>" +
                        "<h4>Social/Emotional Milestones</h4>" +
                        "<ul>" +
                        "<li>Calms down within 10 minutes after you leave</li>" +
                        "<li>Notices and joins other children in play</li>" +
                        "</ul>" +
                        "<h4>Language/Communication Milestones</h4>" +
                        "<ul>" +
                        "<li>Engages in conversations with at least two exchanges</li>" +
                        "<li>Asks questions like 'who,' 'what,' 'where,' or 'why'</li>" +
                        "<li>Describes actions in pictures or books</li>" +
                        "<li>Says first name when asked</li>" +
                        "<li>Is understood most of the time</li>" +
                        "</ul>" +
                        "<h4>Cognitive Milestones</h4>" +
                        "<ul>" +
                        "<li>Draws a circle when shown how</li>" +
                        "<li>Avoids touching hot objects when warned</li>" +
                        "</ul>" +
                        "<h4>Movement/Physical Development Milestones</h4>" +
                        "<ul>" +
                        "<li>Strings items like beads or macaroni</li>" +
                        "<li>Puts on some clothes independently</li>" +
                        "<li>Uses a fork</li>" +
                        "</ul>" +
                        "<h4>Additional Questions to Consider</h4>" +
                        "<ul>" +
                        "<li>Activities you and your child enjoy together</li>" +
                        "<li>Your child's favorite activities</li>" +
                        "<li>Any concerns about your child's behavior</li>" +
                        "<li>Any loss of previously acquired skills</li>" +
                        "<li>Special healthcare needs or premature birth</li>" +
                        "</ul>" +
                        "</section>";

            case "4 year":
                return "<section>" +
                        "<h2>Your Child's 4 Year Health Check</h2>" +
                        "<p>Before starting school, it's recommended to have a comprehensive health check for your child.</p>" +
                        "<h3>The Health Assessment May Include:</h3>" +
                        "<ul>" +
                        "<li>Hearing check</li>" +
                        "<li>Vision test – Statewide Eyesight Preschooler Screening (StEPS)</li>" +
                        "<li>Physical check (height and weight)</li>" +
                        "<li>Oral health assessment</li>" +
                        "<li>Developmental and emotional wellbeing questions</li>" +
                        "<li>Immunisation status review</li>" +
                        "<li>Immunisation History Statement for school enrolment</li>" +
                        "</ul>" +
                        "<p>Discuss any health, development, or family issues that may affect learning with your health professional or teacher.</p>" +
                        "<h3>Statewide Eyesight Preschooler Screening (StEPS) Program</h3>" +
                        "<p>NSW Health offers a free vision screening for all 4-year-old children through the StEPS program. Early detection of vision problems is crucial.</p>" +
                        "<p>If your child doesn't attend preschool or misses the screening, contact your local Child and Family Health Centre or StEPS coordinator. More information is available at <a href='https://www.health.nsw.gov.au/kidsfamilies/MCFHealth/Pages/StEPS.aspx'>NSW Health StEPS Program</a>.</p>" +
                        "<h3>Before School Starts</h3>" +
                        "<p>Attending preschool helps prepare your child for school. Participate in orientation programs and help your child adjust to the new environment.</p>" +
                        "<h4>Tips to Help Your Child Include:</h4>" +
                        "<ul>" +
                        "<li>Express excitement about starting school</li>" +
                        "<li>Attend orientation days</li>" +
                        "<li>Explain basic school rules</li>" +
                        "<li>Familiarize your child with the school facilities</li>" +
                        "<li>Try on uniforms and shoes beforehand</li>" +
                        "<li>Visit the school during playtimes</li>" +
                        "<li>Show after-school care facilities if applicable</li>" +
                        "</ul>" +
                        "<p>For more information, visit the 'starting school' pages at <a href='https://education.nsw.gov.au/early-childhood-education'>education.nsw.gov.au/early-childhood-education</a>.</p>" +
                        "</section>";

            default:
                return "<section>" +
                        "<h2>Check Information</h2>" +
                        "<p>Information about the selected check will be displayed here.</p>" +
                        "</section>";
        }
    }
}
