import { Typography } from "@mui/material";
import { useField } from "formik";
import { FileUploader } from "react-drag-drop-files";

interface Props {
  name: string;
  types: string[];
}

const FileUploadButton: React.FunctionComponent<Props> = ({ name, types }) => {
  const [field, meta, helpers] = useField(name);
  return (
    <>
      <FileUploader
        handleChange={(file) => helpers.setValue(file)}
        name={name}
        types={types}
      />
      {meta.touched && meta.error && (
        <Typography color="error">{meta.error}</Typography>
      )}
    </>
  );
};

export default FileUploadButton;
