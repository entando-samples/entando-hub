import { useEffect, useRef, useState } from "react"
import { Content, TextInput } from "carbon-components-react"
import { tokenSchema } from "../../../../helpers/validation/tokenSchema"
import i18n from "../../../../i18n"
import { CHAR_LENGTH, CHAR_LIMIT_MSG_SHOW_TIME, DESCRIPTION_FIELD_ID, MAX_CHAR_LENGTH, NAME_FIELD_ID } from "../../../../helpers/constants"

const NewToken = ({ onDataChange, validationResult }) => {
  const [token, setToken] = useState({
    name: "",
    tokenData: "",
  })
  const [tokenNameLength, setTokenNameLength] = useState(false);

  const [mounted, setMounted] = useState(false);
  const timerRef = useRef(null);

  const [showNameCharLimitErrMsg, setShowNameCharLimitErrMsg] = useState(false);
  const [showTokenDataCharLimitErrMsg, setShowTokenDataCharLimitErrMsg] = useState(false);

  const changeToken = (field, value) => {
    const newObj = {
      ...token,
    }
    newObj[field] = value
    setToken(newObj)
    onDataChange(newObj)
  }

  const onChangeHandler = (e, fieldName) => {

    if (fieldName === 'name') {
      if (e.target.value.trim().length < CHAR_LENGTH) {
        const msg = e.target.value.trim().length === 0 ? i18n.t('formValidationMsg.nameRequired') : i18n.t('formValidationMsg.min3Char')
        validationResult["name"] = [msg]
      }
      if (e.target.value.trim().length > MAX_CHAR_LENGTH) {
        validationResult["name"] = [i18n.t('formValidationMsg.max25Char')]
      }
      fieldName === 'name' && setTokenNameLength(e.target.value.trim().length)
    }

    changeToken(fieldName, e.target.value)
  }

  /**
   * Handle keyPress event for input fields and show/hide character limit error message
   * @param {*} e
   */
   const keyPressHandler = (e) => {
    if (e.target.id === NAME_FIELD_ID && e.target.value.length >= MAX_CHAR_LENGTH) {
      validationResult[NAME_FIELD_ID] = [i18n.t('formValidationMsg.max25Char')];
      setShowNameCharLimitErrMsg(true);
      timerRef.current = setTimeout(() => disappearCharLimitErrMsg(e.target.id), CHAR_LIMIT_MSG_SHOW_TIME);
    }
  }

  const disappearCharLimitErrMsg = (fieldId) => {
    if (mounted) {
      validationResult[fieldId] = undefined;
      if (fieldId === NAME_FIELD_ID) {
        setShowNameCharLimitErrMsg(false);
      }
    }
  }

  useEffect(() => {
    setMounted(true);
    // Clear the interval when the component unmounts
    return () => {
      setMounted(false);
      clearTimeout(timerRef.current);
    };
  }, []);

  /**
   * @param {*} e Event object to get value of field
   * @param {*} field Name of the field
   * @description Trimming whitespaces from the field value.
   */
  const trimBeforeFormSubmitsHandler = (e, field) => {
    changeToken(field, e.target.value.trim())
  }

  return (
    <>
      <Content>
        <TextInput
          invalid={(tokenNameLength < CHAR_LENGTH || tokenNameLength > MAX_CHAR_LENGTH || showNameCharLimitErrMsg) && !!validationResult["name"]}
          invalidText={
            (tokenNameLength < CHAR_LENGTH || tokenNameLength > MAX_CHAR_LENGTH || showNameCharLimitErrMsg) ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id={NAME_FIELD_ID}
          labelText={`${i18n.t('page.management.label.name')} ${tokenSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          value={token.name}
          onChange={(e) => onChangeHandler(e, "name")}
          maxLength={MAX_CHAR_LENGTH}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "name")}
          onKeyPress={keyPressHandler}
        />
        
      </Content>
    </>
  )
}
export default NewToken
