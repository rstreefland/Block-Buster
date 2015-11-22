package uk.ac.rdg.rhys.blockbuster;

import java.io.File;

import uk.ac.rdg.rhys.breakout.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;

public class MainActivity extends SherlockActivity {
	
	// different game states
	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_WIN = 5;

	// different view states
	public static final int VIEW_SINGLEPLAYER = 0;
	public static final int VIEW_MULTIPLAYER = 1;
	public static final int VIEW_LEVELBUILDER = 2;

	private GameThread mGameThread;
	private GameView mGameView;

	// objects for action bar
	static ActionBar sActionBar;
	private com.actionbarsherlock.view.Menu mActionBarMenu;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		launchMainMenu();

	}

	public void launchMainMenu() {
		
		// reset the action bar back to its default state
		sActionBar = getSupportActionBar();
		sActionBar.setDisplayShowHomeEnabled(true);
		sActionBar.setTitle(R.string.app_name);

		if (mActionBarMenu != null) {
			mActionBarMenu.clear();
		}

		setContentView(R.layout.main_menu);
	}

	public void launchMainMenu(View view) {
		setContentView(R.layout.main_menu);
	}

	public void launchSinglePlayer(View view) {
		initialiseGame(false);
	}

	public void launchMultiplayer(View view) {
		initialiseGame(true);
	}

	public void initialiseGame(final boolean isMultiplayer) {
		setContentView(R.layout.level_selector);

		// get the list of level names
		File[] levels = getFilesDir().listFiles();
		String[] levelNames = new String[levels.length];

		// store the level names in an array
		for (int i = 0; i < levels.length; i++) {
			levelNames[i] = levels[i].getName();
		}

		// create a spinner and set the list of level names as the values
		final Spinner s = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, levelNames);
		s.setAdapter(adapter);

		// listen for user selection
		s.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> aParentView, View aView,
					int aPosition, long anId) {
				
				// if the 'load level' button is pressed
				Button button = (Button) findViewById(R.id.mainmenu_button);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// pass the file name to the correct method
						if (isMultiplayer == false) {
							startSinglePlayer(mGameView, null, s
									.getSelectedItem().toString());
						} else {
							startMultiplayer(mGameView, null, s
									.getSelectedItem().toString());
						}
					}
				});
			}

			@Override
			public void onNothingSelected(AdapterView<?> anAdapterView) {
				// do nothing
			}
		});

		// if the 'load random level button is pressed
		Button button2 = (Button) findViewById(R.id.startgame_button);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// invoke the correct method with a blank filename
				if (isMultiplayer == false) {
					startSinglePlayer(mGameView, null, "");
				} else {
					startMultiplayer(mGameView, null, "");
				}
			}
		});

	}

	private void startSinglePlayer(GameView gView, GameThread gThread,
			String levelName) {
		
		// setup the view and action bar
		setContentView(R.layout.activity_main);
		setupActionBar();

		// set up the single player game 
		mGameView = (GameView) findViewById(R.id.gamearea);
		mGameView.currentLevelName = levelName;
		mGameView.currentView = VIEW_SINGLEPLAYER;
		mGameThread = new SinglePlayer(mGameView);
		mGameView.setThread(mGameThread);
		mGameThread.setState(GameThread.STATE_READY);
	}

	public void startMultiplayer(GameView gView, GameThread gThread,
			String levelName) {
		
		// setup the view and action bar
		setContentView(R.layout.activity_main);
		setupActionBar();

		// set up the multiplayer game
		mGameView = (GameView) findViewById(R.id.gamearea);
		mGameView.currentLevelName = levelName;
		mGameView.currentView = VIEW_MULTIPLAYER;
		mGameThread = new Multiplayer(mGameView);
		mGameView.setThread(mGameThread);
		mGameThread.setState(GameThread.STATE_READY);

	}

	public void launchLevelBuilder(View view) {
		setContentView(R.layout.level_builder);

		// set up the level builder
		mGameView = (GameView) findViewById(R.id.gamearea);
		mGameView.currentView = VIEW_LEVELBUILDER;
		mGameThread = new LevelCrafter(mGameView);
		mGameView.setThread(mGameThread);
		mGameThread.setState(GameThread.STATE_READY);

	}

	public void saveLevel(View view) {
		
		// create a new dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(
				mGameView.getContext());
		View dialog_layout = getLayoutInflater().inflate( 
				R.layout.save_dialog, null);

		// set dialog title and message
		builder.setMessage(R.string.dialog_savelevel_message).setTitle(
				R.string.dialog_savelevel_title);

		final EditText levelName = (EditText) dialog_layout
				.findViewById(R.id.text1);

		// if save button is pressed
		builder.setPositiveButton(R.string.dialog_savelevel_save,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// save the level
						mGameThread.saveLevel(levelName.getText().toString());
						setContentView(R.layout.main_menu);
					}
				});
		
		// if cancel button is pressed
		builder.setNegativeButton(R.string.dialog_savelevel_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

		// show the dialog
		AlertDialog dialog = builder.create();
		dialog.setView(dialog_layout);
		dialog.show();
	}

	public void help(View view) {
		// show the help screen
		setContentView(R.layout.help);
	}

	public void setupActionBar() {
		// set up the action bar to display the score, current level and buttons  
		sActionBar = getSupportActionBar();
		sActionBar.setDisplayShowHomeEnabled(false);
		sActionBar.setTitle("");

		// inflate the menu items for use in the action bar
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_activity_actions, (com.actionbarsherlock.view.Menu) mActionBarMenu);
	}

	public void stopped() {

		// create dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(
				mGameView.getContext());

		// if the 'new game' button is pressed
		builder.setPositiveButton(R.string.dialog_stopped_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mGameThread.doStart();
					}
				});

		// set dialog title and message
		builder.setMessage(
				R.string.dialog_stopped_string)
				.setTitle(R.string.dialog_stopped_title);

		// show the dialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/*
	 * Activity state functions
	 */

	@Override
	protected void onPause() {
		super.onPause();

		if (mGameView != null) {
			if (mGameThread.getMode() == GameThread.STATE_RUNNING) {
				mGameThread.setState(GameThread.STATE_PAUSE);
				mGameThread.pause();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mGameView != null) {

			mGameView.cleanup();
			mGameThread = null;
			mGameView = null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// open the main menu if the back button is pressed on any other screen
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			launchMainMenu();
		}
		return false;
	}

	/*
	 * UI Functions
	 */

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		mActionBarMenu = menu;

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

		switch (item.getItemId()) {
		// if the pause button is pressed
		case R.id.action_pause:
				mGameThread.pause();
			return true;
		// if the stop button is pressed
		case R.id.action_stop:
			mGameThread.setState(GameThread.STATE_STOPPED);
			stopped();
			return true;
		}

		return false;
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing if nothing is selected
	}
}

// This file is part of the course
// "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
//
// You should have received a copy of the GNU General Public License
// along with it. If not, see <http://www.gnu.org/licenses/>.
