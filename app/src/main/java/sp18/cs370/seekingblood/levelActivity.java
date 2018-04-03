package sp18.cs370.seekingblood;

import android.content.pm.ActivityInfo;
import android.gesture.Gesture;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import static java.lang.Math.abs;

public class levelActivity extends AppCompatActivity implements
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private boolean onGround;           // Boolean that checks if the character is on the ground
    private Handler moveHandler;        // Handler used in horizontal movement
    private Handler jumpHandler;        // Handler used in vertical movement
    private int region;                 // Integer that refers to 1 of the 4 movement sub-regions
    private int width;                  // Integer that holds the width of the screen
    private int height;                 // Integer that holds the height of the screen
    private static int initialVelocity;        // Indicates initial vertical jump velocity
    private int currentVelocity;        // Indicates current vertical velocity
    private int gravity;                // Refers to the rate of negative vertical acceleration
    private GestureDetector gestureDetector; // Used to detect unique gestures
    private ImageView testCharacter;    // Refers to a stock image of a knight - can be moved
    private Rect sprintRegionR;         // Sprint Right
    private Rect walkRegionR;           // Walk Right
    private Rect walkRegionL;           // Walk Left
    private Rect sprintRegionL;         // Sprint Left
    private Rect currentRegion;         // Region used in movement
    private Rect movementRegion;        // Region that contains all movement sub-regions
    private Rect actionRegion;          // Region that will capture actions unrelated to movement
    private Rect floorRegion;           // Region that acts as a floor

    /*  REGIONS - Combat and Movement-related regions will have a number associated with them.
     *  Movement regions, starting from the top, are numbered 1-4.
     *  The action region, handling all swipes on the 3-quarter-right-hand side of the screen,
     *  will be numbered 5.
     *  These numbers are used throughout the logic. Perhaps there's an easier way to denote
     *  regions? If not, this method seems to suffice.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        // Forces screen into landscape orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Get screen size in pixels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        // Define the test character, set their location, and set their statuses (Class later)
        testCharacter = findViewById(R.id.characterID);
        testCharacter.setX(width / 2);
        testCharacter.setY(height / 2);
        initialVelocity = -30;
        currentVelocity = initialVelocity;
        gravity = 2;
        onGround = true;
        // Define the region boundaries
        sprintRegionR = new Rect(0, 0, width / 6, height / 4);
        walkRegionR = new Rect(0, height / 4, width / 6, height / 2);
        walkRegionL = new Rect(0, height / 2, width / 6, (3 * height) / 4);
        sprintRegionL = new Rect(0, (3 * height) / 4, width / 6, height);
        movementRegion = new Rect(0, 0, width / 6, height);
        actionRegion = new Rect(width / 6, 0, width, height);
        floorRegion = new Rect(0, (height  * 3) / 4, width, height);
        gestureDetector = new GestureDetector(this);
        // Map the View
        View mainView = findViewById(R.id.baseViewID);
        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                region = GetRegionPressed((int)event.getX(), (int)event.getY()); // Check if a movement region was touched
                if(region == 5) {
                    return gestureDetector.onTouchEvent(event);
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: // Initial Press
                            v.performClick();
                            if (moveHandler != null)
                                return true;
                            moveHandler = new Handler();
                            moveHandler.postDelayed(moveAction, 20);
                            break;
                        case MotionEvent.ACTION_MOVE: // While held and finger isn't steady
                            if (moveHandler == null)
                                return true;
                            else if (region == 1)
                                currentRegion = sprintRegionR;
                            else if (region == 2)
                                currentRegion = walkRegionR;
                            else if (region == 3)
                                currentRegion = walkRegionL;
                            else if (region == 4)
                                currentRegion = sprintRegionL;
                            if (!currentRegion.contains((int) event.getX(), (int) event.getY())) {
                                moveHandler.removeCallbacks(moveAction);
                                moveHandler = null;
                            }
                            break;
                        case MotionEvent.ACTION_UP: // Touch released
                            if (moveHandler == null)
                                return true;
                            moveHandler.removeCallbacks(moveAction);
                            moveHandler = null;
                            break;
                    }
                    return true;
                }
            }
            Runnable moveAction = new Runnable() { // Runnable that repeatedly calls move functions
                @Override
                public void run() {
                    if(region == 1)
                        SprintRight();
                    else if(region == 2)
                        WalkRight();
                    else if(region == 3)
                        WalkLeft();
                    else if(region == 4)
                        SprintLeft();
                    moveHandler.postDelayed(this, 20);
                }
            };
        });
    }

    protected int GetRegionPressed(int x, int y) {
        if(sprintRegionR.contains(x, y)) {
            return 1;
        } else if(walkRegionR.contains(x, y)) {
            return 2;
        } else if(walkRegionL.contains(x, y)) {
            return 3;
        } else if(sprintRegionL.contains(x, y)) {
            return 4;
        } else if(actionRegion.contains(x, y)) {
            return 5;
        } else
            return 0;
    }
    // Since the character is currently only moving horizontally, there is no need to check for upper bounds.
    protected void SprintRight() {
        int x = (int)testCharacter.getX() + 12;
        if(ValidMove(x))
            testCharacter.setX(x);
    }
    protected void WalkRight() {
        int x = (int)testCharacter.getX() + 5;
        if(ValidMove(x))
            testCharacter.setX(x);
    }
    protected void WalkLeft() {
        int x = (int)testCharacter.getX() - 5;
        if(ValidMove(x))
            testCharacter.setX(x);
    }
    protected void SprintLeft() {
        int x = (int)testCharacter.getX() - 12;
        if(ValidMove(x))
            testCharacter.setX(x);
    }
    protected void Jump() {
        int y = (int)testCharacter.getY();
        y += currentVelocity;
        currentVelocity += gravity;
        if(onGround) {
            onGround = false;
            testCharacter.setY(y);
        } else if(!ReachedGround(y)) {
            System.out.println("Mid-jump!");
            testCharacter.setY(y);
        } else {
            System.out.println("Touched floor!");
            testCharacter.setY(height / 2);
            jumpHandler.removeCallbacks(jumpAction);
            jumpHandler = null;
            onGround = true;
            currentVelocity = initialVelocity;
        }
    }
    protected boolean ValidMove(int x) {
        return ((!movementRegion.contains(x, 0)) && (x <= width));
    }
    protected boolean ReachedGround(int y) {
        return ((floorRegion.contains(0, y)));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        // If there is a larger change in X, the swipe must be horizontal.
        // If there is a larger change in Y, the swipe must be vertical.
        int deltaX = abs((int)(event1.getX() - event2.getX()));
        int deltaY = abs((int)(event1.getY() - event2.getY()));
        if(deltaY > deltaX) { // Swipe is vertical
            if(event1.getY() > event2.getY()) {
                System.out.println("Swipe up detected!");
                if(onGround) {
                    System.out.println("Initiating jump!");
                    if (jumpHandler != null)
                        return true;
                    jumpHandler = new Handler();
                    jumpHandler.postDelayed(jumpAction, 20);
                }
            }
            else
                System.out.println("Swipe down detected!");
        } else { // Swipe must be horizontal
            if(event1.getX() > event2.getX())
                System.out.println("Swipe left detected!");
            else
                System.out.println("Swipe right detected!");
        }
        return true;
    }
    Runnable jumpAction = new Runnable() {
        @Override
        public void run() {
            Jump();
            if(jumpHandler != null)
                jumpHandler.postDelayed(jumpAction, 20);
        }
    };

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {

        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {

        return true;
    }
}
