export function Avatar({ name, color }: { name: string; color: string }) {
  const initials = name
    .split(" ")
    .map(n => n[0])
    .join("")
    .toUpperCase();

  return (
    <div
      className="avatar"
      style={{ backgroundColor: color ?? "#ccc" }}
    >
      {initials}
    </div>
  );
}
