import './Input.css'

const Input = ({type, placeholder, value, onChange, style, name, required}) => {
    return (
        <input className={`inputComponent ${style != undefined ? style : ''}`}
        type={type} 
        placeholder={placeholder} 
        value={value}
        onChange={onChange}
        name={name}
        required={required}
        lang="pt-BR"
        >
        </input>
    )
}

export default Input