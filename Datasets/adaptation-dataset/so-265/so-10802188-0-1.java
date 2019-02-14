public class foo {
public static URI relativize(URI base, URI child) {
  // Normalize paths to remove . and .. segments
  base = base.normalize();
  child = child.normalize();

  // Split paths into segments
  String[] bParts = base.getPath().split("\\/");
  String[] cParts = child.getPath().split("\\/");

  // Discard trailing segment of base path
  if (bParts.length > 0 && !base.getPath().endsWith("/")) {
    bParts = Arrays.copyOf(bParts, bParts.length - 1);
  }

  // Remove common prefix segments
  int i = 0;
  while (i < bParts.length && i < cParts.length && bParts[i].equals(cParts[i])) {
    i++;
  }

  // Construct the relative path
  StringBuilder sb = new StringBuilder();
  for (int j = 0; j < (bParts.length - i); j++) {
    sb.append("../");
  }
  for (int j = i; j < cParts.length; j++) {
    if (j != i) {
      sb.append("/");
    }
    sb.append(cParts[j]);
  }

  return URI.create(sb.toString());
}
}