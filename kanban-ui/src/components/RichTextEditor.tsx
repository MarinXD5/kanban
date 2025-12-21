import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import { useEffect } from "react";

export function RichTextEditor({
  value,
  onSave,
}: {
  value: string;
  onSave: (html: string) => void;
}) {
  const editor = useEditor({
    extensions: [StarterKit],
    content: value,
  });

  useEffect(() => {
    if (!editor) return;

    const handler = () => {
      const html = editor.getHTML();
      onSave(html);
    };

    editor.on("blur", handler);
    return () => editor.off("blur", handler);
  }, [editor, onSave]);

  return (
    <div className="rich-editor">
      <EditorContent editor={editor} />
    </div>
  );
}
