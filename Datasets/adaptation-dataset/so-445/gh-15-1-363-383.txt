package fi.aalto.legroup.achso.browsing;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fi.aalto.legroup.achso.R;
import fi.aalto.legroup.achso.app.App;
import fi.aalto.legroup.achso.authoring.QRHelper;
import fi.aalto.legroup.achso.entities.Annotation;
import fi.aalto.legroup.achso.entities.Group;
import fi.aalto.legroup.achso.entities.Video;
import fi.aalto.legroup.achso.playback.PlayerActivity;
import fi.aalto.legroup.achso.storage.VideoRepository;
import fi.aalto.legroup.achso.storage.remote.download.DownloadErrorEvent;
import fi.aalto.legroup.achso.storage.remote.download.DownloadService;
import fi.aalto.legroup.achso.storage.remote.download.DownloadStateEvent;
import fi.aalto.legroup.achso.storage.remote.upload.UploadErrorEvent;
import fi.aalto.legroup.achso.storage.remote.upload.UploadService;
import fi.aalto.legroup.achso.storage.remote.upload.UploadStateEvent;
import fi.aalto.legroup.achso.views.adapters.AnnotationsListAdapter;
import fi.aalto.legroup.achso.views.adapters.GroupsListAdapter;

public final class DetailActivity extends AppCompatActivity
        implements MenuItem.OnMenuItemClickListener {

    public static final String ARG_VIDEO_ID = "ARG_VIDEO_ID";

    private Video video;

    private Bus bus;
    private ListView groupsList;
    private ListView annotationsList;

    private Button uploadButton;
    private Button groupsButton;
    private Button toggleAnnotations;
    private Button addQRButton;

    private CheckBox isAvailableOfflineCheckbox;
    private CheckBox isPublicCheckbox;

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bus = App.bus;
        final UUID videoId = (UUID) getIntent().getSerializableExtra(ARG_VIDEO_ID);

        try {
            video = App.videoRepository.getVideo(videoId).inflate();
        } catch (IOException e) {
            e.printStackTrace();
            SnackbarManager.show(Snackbar.with(this).text(R.string.storage_error));
            finish();
            return;
        }

        setContentView(R.layout.activity_information);

        isAvailableOfflineCheckbox = (CheckBox)  findViewById(R.id.availableOfflineCheckbox);
        isPublicCheckbox = (CheckBox) findViewById(R.id.isVideoPublic);

        groupsList = (ListView) findViewById(R.id.groupsList);
        annotationsList = (ListView) findViewById(R.id.annotationsList);

        groupsButton = (Button)  findViewById(R.id.toggleGroupsList);
        uploadButton = (Button) findViewById(R.id.uploadVideoButton);
        addQRButton = (Button)  findViewById(R.id.addQRButton);
        toggleAnnotations = (Button) findViewById(R.id.toggleAnnotationsList);

        Toolbar bar = (Toolbar)  findViewById(R.id.toolbar);
        setSupportActionBar(bar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        Location location = video.getLocation();

        if (location != null) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

            GoogleMap map = mapFragment.getMap();

            map.addCircle(new CircleOptions()
                    .center(position)
                    .radius(location.getAccuracy())
                    .strokeWidth(3.0f)
                    .strokeColor(Color.WHITE)
                    .fillColor(Color.parseColor("#80ffffff")));

            map.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14.5f));

            findViewById(R.id.unknownLocationText).setVisibility(View.GONE);
        }

        initializeAddQRButton();
        initializeUploadButton();
        initializeAnnotationsButton();
        initializeIsLocal();

        if (!App.videoRepository.isAuthorizedToShareVideo(video.getId())) {
            System.out.println("not authorized!");
            isPublicCheckbox.setEnabled(false);
            groupsButton.setEnabled(false);
            SnackbarManager.show(Snackbar.with(DetailActivity.this).text("You do not have the rights to share this video!"));
        } else {
            initializeGroupsButton();
            initializeIsPublic();
        }

        groupsList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        loadGroups();
        setListViewHeightBasedOnChildren(groupsList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String code = QRHelper.getQRCodeForResult(requestCode, resultCode, data);
        video.setTag(code);
        video.save(new VideoRepository.VideoCallback() {
            @Override
            public void found(Video video) {
                String message = getResources().getString(R.string.qr_tag_success);
                SnackbarManager.show(Snackbar.with(DetailActivity.this).text(message));
            }

            @Override
            public void notFound() {
                String message = getResources().getString(R.string.qr_tag_fail);
                SnackbarManager.show(Snackbar.with(DetailActivity.this).text(message));
            }
        });
    }

    private void initializeAddQRButton() {

        addQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UUID id = video.getId();
                List ids = new ArrayList<>();
                ids.add(id);
                QRHelper.readQRCodeForVideos(DetailActivity.this, ids, null);
            }
        });
    }

    private void initializeGroupsButton() {
        if (video.isLocal()) {
            groupsButton.setEnabled(false);
        }

        groupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupsList.getVisibility() == View.VISIBLE) {
                    groupsList.setVisibility(View.GONE);
                } else {
                    groupsList.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initializeAnnotationsButton() {
        if (video.getAnnotations().size() == 0) {
            toggleAnnotations.setEnabled(false);
        } else {
            toggleAnnotations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (annotationsList.getVisibility() == View.VISIBLE) {
                        annotationsList.setVisibility(View.GONE);
                    } else {
                        annotationsList.setVisibility(View.VISIBLE);
                    }
                }
            });

            loadAnnotations();
            setListViewHeightBasedOnChildren(annotationsList);
        }
    }

    private void initializeIsPublic() {
        if (video.isLocal()) {
            isPublicCheckbox.setEnabled(false);
        }

        isPublicCheckbox.setChecked(video.getIsPublic());

        isPublicCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UUID id = video.getId();
                if (b) {
                    video.setIsPublic(true);
                    App.videoRepository.makeVideoPublic(id);
                } else {
                    video.setIsPublic(false);
                    App.videoRepository.makeVideoPrivate(id);
                }

                video.save(null);
            }
        });
    }

    private void initializeIsLocal() {
        if (video.isLocal()) {
            isAvailableOfflineCheckbox.setEnabled(false);
        }

        isAvailableOfflineCheckbox.setChecked(video.hasCachedFiles());

        isAvailableOfflineCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UUID id = video.getId();

                isAvailableOfflineCheckbox.setEnabled(false);
                if (b) {
                    DownloadService.download(DetailActivity.this, id);

                    isAvailableOfflineCheckbox.setChecked(true);
                } else {
                    try {
                        App.videoRepository.deleteCachedFile(id);
                        isAvailableOfflineCheckbox.setEnabled(true);
                        isAvailableOfflineCheckbox.setChecked(false);
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }
            }
        });
    }

    private void initializeUploadButton() {
        if (!video.isLocal()) {
            uploadButton.setEnabled(false);
            uploadButton.setVisibility(View.GONE);
        } else {
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String currentlyUploading = getString(R.string.currently_uploading);

                    uploadButton.setEnabled(false);
                    uploadButton.setAlpha(.5f);
                    uploadButton.setText(currentlyUploading);

                    UUID id = video.getId();
                    UploadService.upload(DetailActivity.this, id);
                }
            });
        }
    }

    @Subscribe
    public void onDownloadState(DownloadStateEvent event) {
        switch (event.getType()) {
            case SUCCEEDED:
                if (event.getVideoId() == video.getId()) {
                    isAvailableOfflineCheckbox.setEnabled(true);
                }
        }
    }

    @Subscribe
    public void onDownloadError(DownloadErrorEvent event) {
        String message = event.getErrorMessage();
        SnackbarManager.show(Snackbar.with(DetailActivity.this).text(message));
        isAvailableOfflineCheckbox.setEnabled(true);
    }

    @Subscribe
    public void onUploadState(UploadStateEvent event) {
        switch (event.getType()) {
            case SUCCEEDED:
                if (event.getVideoId() == video.getId()) {
                    uploadButton.setVisibility(View.GONE);
                    isPublicCheckbox.setEnabled(true);
                    groupsButton.setEnabled(true);
                }
        }
    }

    @Subscribe
    public void onUploadError(UploadErrorEvent event) {
        String message = event.getErrorMessage();
        SnackbarManager.show(Snackbar.with(DetailActivity.this).text(message));
        uploadButton.setEnabled(true);
        uploadButton.setAlpha(1f);
    }

    // Hack from https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ActionBar.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void goToVideoAtPoint(long timestamp) {
        Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
        UUID id = video.getId();
        intent.putExtra(PlayerActivity.ARG_VIDEO_ID, id);
        intent.putExtra(PlayerActivity.ARG_VIDEO_TIME, timestamp);
        startActivity(intent);
    }

    private class OnAnnotationClicked implements AnnotationsListAdapter.OnAnnotationItemClickedListener{

        public  OnAnnotationClicked() {}

        @Override
        public void onClick(Annotation annotation) {
            long time = annotation.getTime();
            goToVideoAtPoint(time);
        }
    }

    public void loadAnnotations() {
        List<Annotation> annotations = video.getAnnotations();
        AnnotationsListAdapter adapter = new AnnotationsListAdapter(this, R.layout.partial_annotation_list_item, annotations);
        adapter.setListener(new OnAnnotationClicked());
        annotationsList.setAdapter(adapter);
    }

    private class OnGroupShareChanged implements GroupsListAdapter.OnGroupSharedEventListener{

        public  OnGroupShareChanged() {}

        @Override
        public void onClick(Group group, boolean isShared) {
            int groupId = group.getId();
            UUID videoId = video.getId();

            if (isShared) {
                App.videoRepository.addVideoToGroup(videoId, groupId);
            } else {
                App.videoRepository.removeVideoFromGroup(videoId, groupId);
            }
        }
    }

    public void loadGroups() {
        try {
            ArrayList<Group> groups = new ArrayList<Group>(App.videoRepository.getGroups());
            GroupsListAdapter adapter = new GroupsListAdapter(this, R.layout.partial_group_list_item, groups, video.getId());
            adapter.setListener(new OnGroupShareChanged());

            groupsList.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Using the Google Maps API via Google Play Services requires displaying its licence
     * information. We'll show the information in an AlertDialog triggered by a MenuItem.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.about_maps).setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        String licenceInfo = GooglePlayServicesUtil
                .getOpenSourceSoftwareLicenseInfo(DetailActivity.this);

        // Using a WebView is a lot faster than showing the text as the dialog's message
        WebView webView = new WebView(DetailActivity.this);
        webView.loadDataWithBaseURL(null, licenceInfo, "text/plain", null, null);

        new AlertDialog.Builder(DetailActivity.this)
                .setTitle(R.string.about_maps)
                .setView(webView)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        return true;
    }

    public Video getVideo() {
        return video;
    }

}
