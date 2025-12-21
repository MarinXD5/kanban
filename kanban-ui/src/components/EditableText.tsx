import { useEffect, useRef, useState } from "react";

export default function EditableText({
  value,
  placeholder,
  multiline = false,
  onSave,
}: {
  value?: string;
  placeholder?: string;
  multiline?: boolean;
  onSave: (value: string) => void;
}) {
  const [editing, setEditing] = useState(false);
  const [text, setText] = useState(value ?? "");

  const inputRef = useRef<HTMLInputElement>(null);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (!editing) return;
    if (multiline) textareaRef.current?.focus();
    else inputRef.current?.focus();
  }, [editing, multiline]);

  const commit = () => {
    if (text.trim() !== value) {
      onSave(text.trim());
    }
    setEditing(false);
  };

  if (!editing) {
    return (
      <div
        className="editable-display"
        onClick={() => setEditing(true)}
      >
        {value || <span className="placeholder">{placeholder}</span>}
      </div>
    );
  }

  return multiline ? (
    <textarea
      ref={textareaRef}
      className="editable-input"
      value={text}
      onChange={e => setText(e.target.value)}
      onBlur={commit}
      onKeyDown={e => {
        if (e.key === "Escape") setEditing(false);
      }}
    />
  ) : (
    <input
      ref={inputRef}
      className="editable-input"
      value={text}
      onChange={e => setText(e.target.value)}
      onBlur={commit}
      onKeyDown={e => {
        if (e.key === "Enter") commit();
        if (e.key === "Escape") setEditing(false);
      }}
    />
  );
}