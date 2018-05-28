package com.gka.akshara.assesseasy;

import com.akshara.assessment.a3.R;

/**
 * Vault to store global data
 */

public class globalvault {

    public static assessquestion[] questions; // The question set
    public static int backclickedonquestionid; // questionid on which back button was clicked (so that while moving forward again, the animation will not be played until this questionid is reached
    public static int currentquestionid = 0;

    // Passed from the ContainerApp while invoking this activit
    public static long a3app_institutionId;
    public static int  a3app_gradeId;
    public static String a3app_gradeString;
    public static String a3app_childId;
    public static String a3app_language;

    // Background images for Question Pages. An image is randomly picked from this array
    public static int[] QP_BGRND_IMGS = {
            R.drawable.bg_qp_01,
            R.drawable.bg_qp_02,
            R.drawable.bg_qp_03,
            R.drawable.bg_qp_04,
            R.drawable.bg_qp_05,
            R.drawable.bg_qp_06,
            R.drawable.bg_qp_07,
            R.drawable.bg_qp_08
    };

    public static int[] ANIMATION_BGRND_IMGS = { // Background images for the Animation
            R.drawable.anim_bg1,
            R.drawable.anim_bg1,
            R.drawable.anim_bg1
    };

    final static int animationdisplayinterval = 5; // display animation after 'n' questions (i.e 0, 2, 4 etc). (use modulus to find if questionid falls in the interval. questionid % 2 = 0
    static boolean allowskipquestions = true; // If false, user will not be allowed to move to next question unless the current question is answered
    static boolean autosynctelemetry = true; // If true, all unsynced Telemetry data (assessment records) will be read from the local DB and Synced to the Server automatically at the end of the Assessment (if connectivity is available)
    static boolean deleterecordsaftersync = false; // if true, all the telemetry records that are synced to the A3 Server will be flushed out from the device database
    static boolean demomodeifnodb = true; // If set true, the app will run with demo questions in case it fails to read question set from the DB

    public static String[] questionTemplTypes = {

            "FIB_ADD",
            "FIB_SUBTRACT",
            "FIB_MULTIPLY",
            "FIB_DIVISION_NOREMINDER",
            "FIB_DIVISION_WITHREMINDER",
            "MCQ_IMG_IMG",
            "MCQ_IMG_TXT",
            "MCQ_IMG",
            "MCQ_TXT",
            "MTF_IMG_IMG",
            "MTF_IMG_TXT",
            "MTF_TXT_IMG",
            "MTF_TXT_TXT",
            "RAR_IMG",
            "RAR_TXT",
            "TOF_TXT",
            "TOF_TXT_IMG",
            "WORD_TXT_IMG",
            "WORD_TXT"
    };

    public static String[] activities = {

            "qp_arithmetic_add",
            "qp_arithmetic_subtract",
            "qp_arithmetic_multiply_horiz",
            "qp_arithmetic_division_wholenumber",
            "qp_arithmetic_division_withreminder",
            "qp_mcq_image_image",
            "qp_mcq_image_text",
            "qp_mcq_image",
            "qp_mcq_text",
            "qp_mtf_image_image",
            "qp_mtf_image_text",
            "qp_mtf_text_image",
            "qp_mtf_text_text",
            "qp_mtf_image_blank",
            "qp_mtf_text_blank",
            "qp_truefalse_text",
            "qp_truefalse_text_image",
            "qp_fib_image_text",
            "qp_fib_text"
    };
}
