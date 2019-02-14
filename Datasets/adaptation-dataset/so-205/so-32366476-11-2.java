public class foo {
public static String getForegroundApp() {
  File[] files = new File("/proc").listFiles();
  int lowestOomScore = Integer.MAX_VALUE;
  String foregroundProcess = null;

  for (File file : files) {
    if (!file.isDirectory()) {
      continue;
    }

    int pid;
    try {
      pid = Integer.parseInt(file.getName());
    } catch (NumberFormatException e) {
      continue;
    }

    try {
      String cgroup = read(String.format("/proc/%d/cgroup", pid));

      String[] lines = cgroup.split("\n");

      if (lines.length != 2) {
        continue;
      }

      String cpuSubsystem = lines[0];
      String cpuaccctSubsystem = lines[1];

      if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
        // not an application process
        continue;
      }

      if (cpuSubsystem.endsWith("bg_non_interactive")) {
        // background policy
        continue;
      }

      String cmdline = read(String.format("/proc/%d/cmdline", pid));

      if (cmdline.contains("com.android.systemui")) {
        continue;
      }

      int uid = Integer.parseInt(
          cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
      if (uid >= 1000 && uid <= 1038) {
        // system process
        continue;
      }

      int appId = uid - AID_APP;
      int userId = 0;
      // loop until we get the correct user id.
      // 100000 is the offset for each user.
      while (appId > AID_USER) {
        appId -= AID_USER;
        userId++;
      }

      if (appId < 0) {
        continue;
      }

      // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
      // String uidName = String.format("u%d_a%d", userId, appId);

      File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
      if (oomScoreAdj.canRead()) {
        int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
        if (oomAdj != 0) {
          continue;
        }
      }

      int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
      if (oomscore < lowestOomScore) {
        lowestOomScore = oomscore;
        foregroundProcess = cmdline;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  return foregroundProcess;
}
}