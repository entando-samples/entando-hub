import * as Yup from "yup"

export const tokenSchema = Yup.object().shape({
  name: Yup.string()
    .min(3, "min3Char")
    .max(25, "max25Char")
    .required("nameRequired"),
  tokenData: Yup.string()
    .max(50, "tokenData"),
})
